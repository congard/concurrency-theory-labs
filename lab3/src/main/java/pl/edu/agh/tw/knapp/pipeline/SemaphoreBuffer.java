package pl.edu.agh.tw.knapp.pipeline;

import pl.edu.agh.tw.knapp.Buffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreBuffer<T> implements Buffer<T> {
    private final List<T> buffer;
    private int bufferPos = 0;
    private int bufferActualSize = 0;

    private final long timeoutMs;

    private final Semaphore readyPortions = new Semaphore(0);
    private final Semaphore availablePositions;

    public SemaphoreBuffer(int size) {
        this(size, 1000L);
    }

    public SemaphoreBuffer(int size, long timeoutMs) {
        this.timeoutMs = timeoutMs;
        buffer = new ArrayList<>(Collections.nCopies(size, null));
        availablePositions = new Semaphore(size);
    }

    private boolean tryAcquireUninterruptibly(Semaphore semaphore) {
        try {
            if (timeoutMs > 0L) {
                return semaphore.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS);
            } else {
                semaphore.acquire();
                return true;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean put(T val) {
        if (!tryAcquireUninterruptibly(availablePositions))
            return false;

        synchronized (this) {
            var index = (bufferPos + bufferActualSize) % buffer.size();
            ++bufferActualSize;
            buffer.set(index, val);
        }

        readyPortions.release();

        return true;
    }

    @Override
    public Optional<T> get() {
        if (!tryAcquireUninterruptibly(readyPortions))
            return Optional.empty();

        T result;

        synchronized (this) {
            result = buffer.get(bufferPos);
            bufferPos = (bufferPos + 1) % buffer.size();
            --bufferActualSize;
        }

        availablePositions.release();

        return Optional.of(result);
    }
}
