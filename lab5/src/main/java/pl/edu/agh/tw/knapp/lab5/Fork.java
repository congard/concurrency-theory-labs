package pl.edu.agh.tw.knapp.lab5;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Fork {
    private final Semaphore semaphore = new Semaphore(1);
    private Table table;

    public Fork() {
        // empty
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Table getTable() {
        return table;
    }

    public void acquire() {
        semaphore.acquireUninterruptibly();
    }

    public boolean acquire(long timeoutMs) {
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

    public void release() {
        semaphore.release();
    }

    public boolean isAvailable() {
        return semaphore.availablePermits() == 1;
    }
}
