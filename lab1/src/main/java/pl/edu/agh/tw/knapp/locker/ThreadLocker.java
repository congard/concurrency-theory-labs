package pl.edu.agh.tw.knapp.locker;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ThreadLocker implements ILocker {
    // <thread id, thread index>
    private final Map<Long, Integer> threadIds;

    private volatile int activeThreadIndex = 0;

    public ThreadLocker(Thread... threads) {
        threadIds = IntStream.range(0, threads.length)
                .mapToObj(i -> new AbstractMap.SimpleEntry<>(threads[i].getId(), i))
                .collect(Collectors.toUnmodifiableMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    @Override
    public void lock() {
        while (!canBeLocked()) {
            Thread.yield();
        }
    }

    @Override
    public void unlock() {
        activeThreadIndex = (activeThreadIndex + 1) % threadIds.size();
    }

    private boolean canBeLocked() {
        var currentThreadIndex = threadIds.get(Thread.currentThread().getId());
        return currentThreadIndex == activeThreadIndex;
    }
}
