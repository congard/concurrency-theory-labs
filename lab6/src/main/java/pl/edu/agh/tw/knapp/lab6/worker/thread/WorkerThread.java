package pl.edu.agh.tw.knapp.lab6.worker.thread;

import pl.edu.agh.tw.knapp.lab6.DatabaseDriver;
import pl.edu.agh.tw.knapp.lab6.RandomSleeper;
import pl.edu.agh.tw.knapp.lab6.worker.factory.WorkerFactory;

public abstract class WorkerThread<T> extends Thread {
    protected final WorkerFactory<T> factory;
    protected final DatabaseDriver<T> driver;
    private final RandomSleeper sleeper;
    private final int iterCount;

    public WorkerThread(WorkerFactory<T> factory, DatabaseDriver<T> driver,
                        int delayMinMs, int delayMaxMs, int iterCount)
    {
        this.factory = factory;
        this.driver = driver;
        this.sleeper = new RandomSleeper(delayMinMs, delayMaxMs);
        this.iterCount = iterCount;
    }

    @Override
    public void run() {
        for (int i = 0; i < iterCount; ++i) {
            onIter(i);

            try {
                sleeper.sleep();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected abstract void onIter(int i);
}
