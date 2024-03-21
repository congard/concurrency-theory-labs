package pl.edu.agh.tw.knapp.lab5.simultaneous;

import pl.edu.agh.tw.knapp.lab5.Arbiter;
import pl.edu.agh.tw.knapp.lab5.Philosopher;

/**
 * Arbiter that guarantees that philosophers can only pick up
 * both forks or none simultaneously.
 */
public class ForkPool extends Arbiter {
    private synchronized boolean tryAcquireForks(Philosopher philosopher) {
        var table = getTable();
        var left = table.getLeftFork(philosopher);
        var right = table.getRightFork(philosopher);

        if (left.isAvailable() && right.isAvailable()) {
            left.acquire();
            right.acquire();
            return true;
        }

        return false;
    }

    @Override
    public synchronized void acquireForks(Philosopher philosopher) {
        while (!tryAcquireForks(philosopher)) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public synchronized void releaseForks(Philosopher philosopher) {
        var table = getTable();
        table.getLeftFork(philosopher).release();
        table.getRightFork(philosopher).release();
        notifyAll();
    }
}
