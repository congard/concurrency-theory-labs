package pl.edu.agh.tw.knapp.lab7.demo;

import pl.edu.agh.tw.knapp.lab7.activeobject.buffer.BufferProxy;

public abstract class Worker<T> extends Thread {
    private final static Logger logger = Logger.getInstance();

    private final RandomSleeper randomSleeper;
    private final int iterCount;

    protected final int timeoutMs;
    protected final BufferProxy<T> bufferProxy;

    public Worker(int delayMinMs, int delayMaxMs, int timeoutMs, int iterCount, BufferProxy<T> bufferProxy) {
        this.randomSleeper = new RandomSleeper(delayMinMs, delayMaxMs);
        this.timeoutMs = timeoutMs;
        this.iterCount = iterCount;
        this.bufferProxy = bufferProxy;
    }

    @Override
    public void run() {
        for (int i = 0; i < iterCount; i++) {
            if (!onIter(i))
                break;

            try {
                randomSleeper.sleep();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        log("Done");
    }

    protected abstract boolean onIter(int iteration);

    protected void log(Object o) {
        logger.log(String.format("%s (tid %s)", getClass().getSimpleName(), getId()), o);
    }
}
