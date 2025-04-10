package com.remaslover;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final int NUMBER_REDUCE = 3;
    public static void main(String[] args) {

        List<String> inputFiles = readFiles();
        Coordinator coordinator = new Coordinator(inputFiles, NUMBER_REDUCE);
        coordinator.start();
        logger.info("MapReduce processing completed successfully.");
    }

    private static List<String> readFiles() {
        List<String> fileNames = new ArrayList<>();
        File folder = new File("./");
        if (folder.isDirectory()) {
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    fileNames.add(file.getAbsolutePath());
                }
            }
        }
        return fileNames;
    }
}