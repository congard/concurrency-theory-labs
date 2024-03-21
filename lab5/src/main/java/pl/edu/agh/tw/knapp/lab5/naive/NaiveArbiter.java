package pl.edu.agh.tw.knapp.lab5.naive;

import pl.edu.agh.tw.knapp.lab5.Arbiter;
import pl.edu.agh.tw.knapp.lab5.Philosopher;

/**
 * Naive arbiter. May cause a deadlock.
 */
public class NaiveArbiter extends Arbiter {
    @Override
    public void acquireForks(Philosopher philosopher) {
        getTable().getLeftFork(philosopher).acquire();
        getTable().getRightFork(philosopher).acquire();
    }

    @Override
    public void releaseForks(Philosopher philosopher) {
        getTable().getLeftFork(philosopher).release();
        getTable().getRightFork(philosopher).release();
    }
}
