package com.remaslover;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Coordinator {
    private static final Logger log = Logger.getLogger(Coordinator.class.getName());
    private static final int NUM_THREADS = 4;
    private final List<String> files;
    private final int numberReduce;
    private final ExecutorService executor;
    private final CountDownLatch mapLatch;
    private final CountDownLatch reduceLatch;


    public Coordinator(List<String> files, int numberReduce) {
        this.files = files;
        this.numberReduce = numberReduce;
        this.executor = Executors.newFixedThreadPool(NUM_THREADS);
        this.mapLatch = new CountDownLatch(files.size());
        this.reduceLatch = new CountDownLatch(numberReduce);
    }

    public void start() {
        for (int i = 0; i < files.size(); i++) {
            Task task = new Task(i, numberReduce, TaskType.MAP, files.get(i), files.size());
            log.info("Starting task " + task);
            executor.submit(new Worker(task, mapLatch));
        }

        try {
            if (!mapLatch.await(15, TimeUnit.SECONDS)) {
                log.severe("Map tasks did not complete");
                handleTaskFailure();
                return;
            }
            log.info("All map tasks completed");
        } catch (InterruptedException e) {
            log.log(Level.SEVERE, "Interrupted while waiting for map tasks", e);
            handleTaskFailure();
            return;
        }

        for (int i = 0; i < numberReduce; i++) {
            Task task = new Task(i, numberReduce, TaskType.REDUCE, null, files.size());
            log.info("Starting task " + task);
            executor.submit(new Worker(task, reduceLatch));
        }

        try {
            if (!reduceLatch.await(15, TimeUnit.SECONDS)) {
                log.severe("Reduce tasks did not complete");
                handleTaskFailure();
                return;
            }
            log.info("All reduce tasks completed");
        } catch (InterruptedException e) {
            log.log(Level.SEVERE, "Interrupted while waiting for reduce tasks", e);
            handleTaskFailure();
            return;
        }

        executor.shutdown();
    }

    private void handleTaskFailure() {
        log.severe("Task failure detected. Shutting down executor and cleaning up resources.");
        executor.shutdownNow();
    }
}
