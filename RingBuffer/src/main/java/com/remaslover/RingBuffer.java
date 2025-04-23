package com.remaslover;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RingBuffer<T> {
    private final int capacity;
    private final AtomicInteger readSequence;
    private final AtomicInteger writeSequence;
    private static final int DEFAULT_CAPACITY = 20;
    private final T[] data;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public RingBuffer(int capacity) {
        this.capacity = (capacity < 1) ? DEFAULT_CAPACITY : capacity;
        this.data = (T[]) new Object[this.capacity];
        this.readSequence = new AtomicInteger(0);
        this.writeSequence = new AtomicInteger(-1);
    }

    public boolean offer(T value) {
        lock.writeLock().lock();
        try {
            boolean isFull = (writeSequence.get() - readSequence.get()) + 1 == capacity;
            if (!isFull) {
                int nextWriteSequence = writeSequence.incrementAndGet();
                data[nextWriteSequence % capacity] = value;
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public T poll() {
        lock.writeLock().lock();
        try {
            boolean isEmpty = writeSequence.get() < readSequence.get();
            if (!isEmpty) {
                T nextValue = data[readSequence.get() % capacity];
                readSequence.getAndIncrement();
                return nextValue;
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getCapacity() {
        return capacity;
    }


}
