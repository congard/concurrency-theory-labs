package pl.edu.agh.tw.knapp.counter;

import pl.edu.agh.tw.knapp.locker.ThreadLocker;

public class SynchronizedCounter extends Counter {
    private final ThreadLocker locker;

    public SynchronizedCounter(int n, Thread... threads) {
        super(n);
        locker = new ThreadLocker(threads);
    }

    @Override
    public void inc() {
        locker.lock();
        super.inc();
        locker.unlock();
    }

    @Override
    public void dec() {
        locker.lock();
        super.dec();
        locker.unlock();
    }
}
