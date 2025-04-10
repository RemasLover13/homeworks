package com.remaslover;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Worker implements Runnable {
    private final Task task;
    private final CountDownLatch latch;
    private static final Logger log = Logger.getLogger(Worker.class.getName());

    public Worker(Task task, CountDownLatch latch) {
        this.task = task;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            if (task.getTaskType() == TaskType.MAP) {
                executeMapTask();
            } else if (task.getTaskType() == TaskType.REDUCE) {
                executeReduceTask();
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error while running task", e);
        } finally {
            latch.countDown();
        }
    }

    private void executeMapTask() throws IOException {
        String content = Files.readString(Paths.get(task.getFile()));
        List<KeyValue> keyValues = mapFunction(task.getFile(), content);

        Map<Integer, List<KeyValue>> buckets = new HashMap<>();
        for (KeyValue kv : keyValues) {
            int bucketId = Math.abs(kv.getKey().hashCode()) % task.getNumberReduce();
            buckets.computeIfAbsent(bucketId, k -> new ArrayList<>()).add(kv);
        }

        for (Map.Entry<Integer, List<KeyValue>> entry : buckets.entrySet()) {
            String fileName = "mr-" + task.getId() + "-" + entry.getKey();
            writeToFile(fileName, entry.getValue());
        }
    }

    private void executeReduceTask() throws IOException {
        List<KeyValue> keyValues = readIntermediateFiles(task.getId());
        keyValues.sort(Comparator.comparing(KeyValue::getKey));

        Map<String, List<String>> groupedValues = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (KeyValue kv : keyValues) {
            groupedValues.computeIfAbsent(kv.getKey(), k -> new ArrayList<>()).add(kv.getValue());
        }

        for (Map.Entry<String, List<String>> entry : groupedValues.entrySet()) {
            String result = reduceFunction(entry.getKey(), entry.getValue());
            writeToFile("final-" + task.getId(), entry.getKey() + " " + result);
        }
    }

    private List<KeyValue> mapFunction(String fileName, String content) {
        List<KeyValue> result = new ArrayList<>();
        String[] words = content.split("\\s+");
        for (String word : words) {
            if (!word.isEmpty()) {
                word = word.replaceAll("[^a-zA-Z]", "");
                result.add(new KeyValue(word, "1"));
            }
        }
        return result;
    }

    private String reduceFunction(String key, List<String> values) {
        return String.valueOf(values.size());
    }

    private List<KeyValue> readIntermediateFiles(int reduceId) throws IOException {
        List<KeyValue> result = new ArrayList<>();
        int numberMapTasks = task.getNumberMapTasks();

        for (int mapTaskId = 0; mapTaskId < numberMapTasks; mapTaskId++) {
            String fileName = "mr-" + mapTaskId + "-" + reduceId;
            if (Files.exists(Paths.get(fileName))) {
                result.addAll(readFromFile(fileName));
            } else {
                log.severe("File not found: " + fileName);
            }
        }
        return result;
    }

    private void writeToFile(String fileName, List<KeyValue> data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (KeyValue kv : data) {
                writer.write(kv.getKey() + "\t" + kv.getValue());
                writer.newLine();
            }
        }
    }

    private void writeToFile(String fileName, String line) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(line);
            writer.newLine();
        }
    }

    private List<KeyValue> readFromFile(String fileName) throws IOException {
        List<KeyValue> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 2) {
                    result.add(new KeyValue(parts[0], parts[1]));
                } else {
                    log.severe("Invalid line in file " + fileName + ": " + line);
                }
            }
        }
        return result;
    }
}