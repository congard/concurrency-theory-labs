package pl.edu.agh.tw.knapp.lab5.naive;

import pl.edu.agh.tw.knapp.lab5.Arbiter;
import pl.edu.agh.tw.knapp.lab5.Logger;
import pl.edu.agh.tw.knapp.lab5.Philosopher;

public class AdvancedNaiveArbiter extends Arbiter {
    private static final Logger logger = Logger.getInstance();
    private final long acquireTimeout;
    private final int attempts;

    public AdvancedNaiveArbiter(long acquireTimeout, int attempts) {
        this.acquireTimeout = acquireTimeout;
        this.attempts = attempts;
    }

    public AdvancedNaiveArbiter() {
        this(50, 15);
    }

    private void waitCollision(Philosopher philosopher, int i) {
        try {
            Thread.sleep((philosopher.getIndex() + 1) * (long) Math.pow(2, i + 1));
        } catch (InterruptedException e) {
            log(e);
        }
    }

    @Override
    public void acquireForks(Philosopher philosopher) {
        var table = getTable();
        var left = table.getLeftFork(philosopher);
        var right = table.getRightFork(philosopher);

        for (int i = 0; i < attempts; ++i) {
            //log(String.format("Philosopher #%s, attempt %s", philosopher.getIndex(), i));

            if (!left.acquire(acquireTimeout)) {
                waitCollision(philosopher, i);
                continue;
            }

            if (!right.acquire(acquireTimeout)) {
                left.release();
                waitCollision(philosopher, i);
                continue;
            }

            return;
        }

        throw new ForkAcquireException(philosopher);
    }

    @Override
    public void releaseForks(Philosopher philosopher) {
        var table = getTable();
        table.getLeftFork(philosopher).release();
        table.getRightFork(philosopher).release();
    }

    private void log(Object o) {
        logger.log(getClass().getSimpleName(), o);
    }
}
