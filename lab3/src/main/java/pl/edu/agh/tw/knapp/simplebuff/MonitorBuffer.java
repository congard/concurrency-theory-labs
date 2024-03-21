package pl.edu.agh.tw.knapp.simplebuff;

import pl.edu.agh.tw.knapp.Buffer;

import java.util.Optional;

// https://en.wikipedia.org/wiki/Producer%E2%80%93consumer_problem#Using_monitors
public class MonitorBuffer<T> implements Buffer<T> {
    private T val;
    private long timeoutMs = 1000;

    private final ConditionMonitor nonEmptyMonitor = new ConditionMonitor();
    private final ConditionMonitor emptyMonitor = new ConditionMonitor();

    public MonitorBuffer() {
        // empty
    }

    public MonitorBuffer(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public void setTimeout(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public long getTimeout() {
        return timeoutMs;
    }

    @Override
    public boolean put(T val) {
        synchronized (emptyMonitor) {
            if (!emptyMonitor.waitFor(() -> this.val == null, timeoutMs))
                return false;

            synchronized (nonEmptyMonitor) {
                this.val = val;
                nonEmptyMonitor.notify();
                return true;
            }
        }
    }

    @Override
    public Optional<T> get() {
        synchronized (nonEmptyMonitor) {
            if (!nonEmptyMonitor.waitFor(() -> this.val != null, timeoutMs))
                return Optional.empty();

            var result = val;

            synchronized (emptyMonitor) {
                val = null;
                emptyMonitor.notify();
            }

            return Optional.of(result);
        }
    }
}
