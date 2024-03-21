package pl.edu.agh.tw.knapp;

public class SynchronizedCounter extends Counter {
    private final ISemaphore semaphore;

    public SynchronizedCounter(int n, ISemaphore semaphore) {
        super(n);
        this.semaphore = semaphore;
    }

    @Override
    public void inc() {
        semaphore.P();
        super.inc();
        semaphore.V();
    }

    @Override
    public void dec() {
        semaphore.P();
        super.dec();
        semaphore.V();
    }
}
