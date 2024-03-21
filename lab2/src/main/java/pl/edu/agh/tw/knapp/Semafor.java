package pl.edu.agh.tw.knapp;

class Semafor implements ISemaphore {
    private boolean _stan = true;
    private int _czeka = 0;

    public Semafor() {
    }

    @Override
    public synchronized void P() {
        ++_czeka;

        while (!_stan) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        _stan = false;
        --_czeka;
    }

    @Override
    public synchronized void V() {
        _stan = true;

        if (_czeka > 0) {
            notify();
        }
    }
}
