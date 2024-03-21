package pl.edu.agh.tw.knapp;

public class Semaphore implements ISemaphore {
    private int value;

    public Semaphore(int initialValue) {
        value = initialValue;
    }

    @Override
    public synchronized void P() {
        while (value == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        --value;
    }

    @Override
    public synchronized void V() {
        ++value;

        if (value == 1) {
            notify();
        }
    }
}
