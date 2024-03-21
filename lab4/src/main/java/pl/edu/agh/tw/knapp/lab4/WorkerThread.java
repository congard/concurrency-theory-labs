package pl.edu.agh.tw.knapp.lab4;

import java.util.Random;
import java.util.function.Function;

public class WorkerThread<T> extends Thread {
    private final static Logger logger = Logger.getInstance();

    protected final Buffer<T> buff;
    private final RandomSleeper randomSleeper;

    private final int iterCount;

    private final Random randM = new Random();
    private final int m;

    /**
     * The main constructor
     * @param buff The buffer to get from
     * @param delayMinMs The minimum random delay, in ms
     * @param delayMaxMs The maximum random delay, in ms
     * @param iterCount The number of iterations
     * @param m The consumed element count upper bound
     */
    public WorkerThread(Buffer<T> buff, long delayMinMs, long delayMaxMs, int iterCount, int m) {
        this.buff = buff;
        randomSleeper = new RandomSleeper(delayMinMs, delayMaxMs);
        this.iterCount = iterCount;
        this.m = m;
    }

    public WorkerThread(Buffer<T> buff) {
        this(buff, 0, 0, 100, 10);
    }

    public int getIterCount() {
        return iterCount;
    }

    protected void randomDelay() throws InterruptedException {
        randomSleeper.sleep();
    }

    protected void log(Object o) {
        logger.log(String.format("%s id %s", getClass().getSimpleName(), getId()), o);
    }

    protected void iterate(Function<Integer, Boolean> function) {
        for (int i = 0; i < iterCount; i++) {
            try {
                randomDelay();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (!function.apply(i)) {
                break;
            }
        }
    }

    protected int getRandomizedM() {
        return randM.nextInt(m) + 1;
    }
}
