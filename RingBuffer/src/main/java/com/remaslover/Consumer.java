package com.remaslover;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


public class Consumer<T> implements Callable<T[]> {
    private final RingBuffer<T> buffer;
    private final int expectedCount;

    public Consumer(RingBuffer<T> buffer, int expectedCount) {
        this.buffer = buffer;
        this.expectedCount = expectedCount;
    }

    @Override
    public T[] call() {
        List<T> items = new ArrayList<>(expectedCount);

        for (int i = 0; i < expectedCount; i++) {
            try {
                T item = buffer.poll();
                items.add(item);
                System.out.println("Consumed: " + item);
                Thread.sleep(100);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                System.out.println("Consumer has interrupted");
                throw new RuntimeException("Consumer was interrupted", e);
            }
        }

        T[] result = (T[]) java.lang.reflect.Array.newInstance(
                items.get(0).getClass(), items.size()
        );
        return items.toArray(result);
    }
}