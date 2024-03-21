package pl.edu.agh.tw.knapp.lab5;

public class Philosopher extends Thread {
    private final static Logger logger = Logger.getInstance();
    private final RandomSleeper randomSleeper;
    private final Arbiter arbiter;
    private Table table;

    private final int iterCount;
    private int index = -1;
    private int counter = 0;

    public Philosopher(Arbiter arbiter, long delayMinMs, long delayMaxMs, int iterCount) {
        this.arbiter = arbiter;
        this.randomSleeper = new RandomSleeper(delayMinMs, delayMaxMs);
        this.iterCount = iterCount;
    }

    public void setTable(Table table) {
        this.table = table;
        index = table.philosophers().indexOf(this);
    }

    public Table getTable() {
        return table;
    }

    public int getIndex() {
        return index;
    }

    protected void log(Object o) {
        logger.log(String.format("%s #%s", getClass().getSimpleName(), getIndex()), o);
    }

    @Override
    public void run() {
        while (counter != iterCount) {
            arbiter.acquireForks(this);

            // jedzenie
            try {
                randomSleeper.sleep();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            arbiter.releaseForks(this);

            ++counter;

            if (counter % 100 == 0) {
                log("Jadlem " + counter + " razy");
            }
        }
    }
}
