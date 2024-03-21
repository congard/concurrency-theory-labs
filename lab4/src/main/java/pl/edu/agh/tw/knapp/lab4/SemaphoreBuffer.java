package pl.edu.agh.tw.knapp.lab4;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreBuffer<T> implements Buffer<T> {
    private final List<T> buffer;
    private int bufferPos = 0;
    private int bufferActualSize = 0;

    private final long timeoutMs;

    private final Semaphore readyPortions;
    private final Semaphore availablePositions;

    public SemaphoreBuffer(int size) {
        this(size, 1000L);
    }

    public SemaphoreBuffer(int size, long timeoutMs) {
        this.timeoutMs = timeoutMs;
        buffer = new ArrayList<>(Collections.nCopies(size, null));
        readyPortions = new Semaphore(0);
        availablePositions = new Semaphore(size);
    }

    private boolean tryAcquireUninterruptibly(Semaphore semaphore, int permits) {
        try {
            if (timeoutMs > 0L) {
                return semaphore.tryAcquire(permits, timeoutMs, TimeUnit.MILLISECONDS);
            } else {
                semaphore.acquire(permits);
                return true;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void addAll(List<T> values) {
        for (var val : values) {
            var index = (bufferPos + bufferActualSize) % capacity();
            ++bufferActualSize;
            buffer.set(index, val);
        }
    }

    @Override
    public boolean put(List<T> values) {
        if (values.size() > capacity())
            throw new BufferOverflowException();

        if (!tryAcquireUninterruptibly(availablePositions, values.size()))
            return false;

        addAll(values);

        readyPortions.release(values.size());

        return true;
    }

    private synchronized List<T> getAll(int amount) {
        List<T> result = new ArrayList<>(amount);

        for (int i = 0; i < amount; ++i) {
            result.add(buffer.get(bufferPos));
            bufferPos = (bufferPos + 1) % capacity();
            --bufferActualSize;
        }

        return result;
    }

    @Override
    public List<T> get(int amount) {
        if (amount > capacity())
            throw new BufferUnderflowException();

        if (!tryAcquireUninterruptibly(readyPortions, amount))
            return List.of();

        var result = getAll(amount);

        availablePositions.release(amount);

        return result;
    }

    @Override
    public int capacity() {
        return buffer.size();
    }
}
