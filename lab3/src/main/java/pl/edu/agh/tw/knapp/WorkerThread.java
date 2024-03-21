package pl.edu.agh.tw.knapp;

public class WorkerThread<T> extends Thread {
    protected final Buffer<T> buff;
    private final RandomSleeper randomSleeper;

    public WorkerThread(Buffer<T> buff, long delayMinMs, long delayMaxMs) {
        this.buff = buff;
        randomSleeper = new RandomSleeper(delayMinMs, delayMaxMs);
    }

    public WorkerThread(Buffer<T> buff) {
        this(buff, 0, 0);
    }
    
    protected void randomDelay() throws InterruptedException {
        randomSleeper.sleep();
    }

    protected void log(Object o) {
        System.out.printf("[%s id %s] %s\n", getClass().getSimpleName(), getId(), o);
    }
}
