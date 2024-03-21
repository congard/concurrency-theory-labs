package pl.edu.agh.tw.knapp.lab5.waiter;

import pl.edu.agh.tw.knapp.lab5.Arbiter;
import pl.edu.agh.tw.knapp.lab5.Fork;
import pl.edu.agh.tw.knapp.lab5.Philosopher;
import pl.edu.agh.tw.knapp.lab5.Table;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Arbiter that allows maximum to n-1 philosophers
 * to sit down at any time. This guarantees at least
 * one philosopher may always acquire both forks,
 * allowing the system to make progress.
 */
public class Waiter extends Arbiter {
    private final Map<Philosopher, Pair<Fork, Fork>> usedForks = new ConcurrentHashMap<>();
    private ArrayBlockingQueue<Fork> availableForks;

    private int eatingCount = 0;
    private int n;

    @Override
    public void setTable(Table table) {
        super.setTable(table);

        n = table.philosophers().size();

        var forks = table.forks();
        availableForks = new ArrayBlockingQueue<>(forks.size());
        availableForks.addAll(forks);
    }

    @Override
    public void acquireForks(Philosopher philosopher) {
        try {
            synchronized (this) {
                while (eatingCount == n - 1)
                    wait();
                ++eatingCount;
            }

            Fork first = availableForks.take();
            Fork second = availableForks.take();

            usedForks.put(philosopher, new Pair<>(first, second));

            first.acquire();
            second.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void releaseForks(Philosopher philosopher) {
        var forks = usedForks.remove(philosopher);
        forks.first().release();
        forks.second().release();

        availableForks.add(forks.first());
        availableForks.add(forks.second());

        synchronized (this) {
            --eatingCount;
            notify();
        }
    }
}
