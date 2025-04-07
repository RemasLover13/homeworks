package com.remaslover;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) {
        RingBuffer<String> buffer = new RingBuffer<>(9);
        String[] goods = {"Bread", "Meat", "Jam", "Milk", "Salt", "Fish", "Pepper", "Tomato"};
        int expectedCount = goods.length;

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Producer<String> producer = new Producer<>(buffer, goods);
        Consumer<String> consumer = new Consumer<>(buffer, expectedCount);

        executor.submit(producer);
        Future<String[]> future = executor.submit(consumer);

        try {
            String[] consumedGoods = future.get();
            System.out.println("All consumed goods: " + String.join(", ", consumedGoods));
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }
}