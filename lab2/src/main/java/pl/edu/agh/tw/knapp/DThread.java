package pl.edu.agh.tw.knapp;

public class DThread extends Thread {
    private final int count;

    private Counter counter;

    public DThread(int count) {
        this.count = count;
    }

    public void setCounter(Counter counter) {
        this.counter = counter;
    }

    @Override
    public void run() {
        for (int i = 0; i < count; i++) {
            counter.dec();
        }
    }
}
