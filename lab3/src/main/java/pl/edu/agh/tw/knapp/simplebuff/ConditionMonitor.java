package pl.edu.agh.tw.knapp.simplebuff;

public class ConditionMonitor {
    public boolean waitFor(Condition cond, long timeoutMs) {
        while (cond.isNotSatisfied()) {
            try {
                long startTime = System.currentTimeMillis();

                wait(timeoutMs);

                if (timeoutMs > 0L && System.currentTimeMillis() - startTime >= timeoutMs && cond.isNotSatisfied()) {
                    return false;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }

    public boolean waitFor(Condition cond) {
        return waitFor(cond, 0L);
    }

    public interface Condition {
        boolean isSatisfied();

        default boolean isNotSatisfied() {
            return !isSatisfied();
        }
    }
}
