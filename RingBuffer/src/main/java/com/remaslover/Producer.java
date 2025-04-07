package com.remaslover;

public class Producer<T> implements Runnable {
    private final RingBuffer<T> buffer;
    private final T[] items;

    public Producer(RingBuffer<T> buffer, T[] items) {
        this.buffer = buffer;
        this.items = items;
    }

    @Override
    public void run() {
        for (T item : items) {
            try {
                buffer.offer(item);
                System.out.println("Produced: " + item);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                System.out.println("Producer has interrupted");
            }
        }
    }
}