package pl.edu.agh.tw.knapp.simplebuff;

import pl.edu.agh.tw.knapp.Buffer;

import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreBuffer<T> implements Buffer<T> {
    private T val;
    private long timeoutMs = 1000;

    private final Semaphore readyPortions = new Semaphore(0);
    private final Semaphore availablePositions = new Semaphore(1);

    public SemaphoreBuffer() {
        // empty
    }

    public SemaphoreBuffer(long timeoutMs) {
        this.timeoutMs = timeoutMs;
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
            this.val = val;
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
            result = val;
            val = null;
        }

        availablePositions.release();

        return Optional.of(result);
    }
}
