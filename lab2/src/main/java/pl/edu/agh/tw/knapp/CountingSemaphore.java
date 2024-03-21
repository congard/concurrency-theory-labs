package pl.edu.agh.tw.knapp;

public class CountingSemaphore implements ISemaphore {
    private final Semafor threadLocker = new Semafor();
    private final Semafor counterLocker = new Semafor();

    private int counter;

    public CountingSemaphore(int initialValue) {
        counter = initialValue;
    }

    @Override
    public void P() {
        counterLocker.P();

        if (counter > 0) {
            --counter;
            counterLocker.V();
        } else {
            counterLocker.V();
            threadLocker.P();
            P();
        }
    }

    @Override
    public void V() {
        counterLocker.P();

        if (++counter == 1) {
            threadLocker.V();
        }

        counterLocker.V();
    }
}
