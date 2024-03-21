package pl.edu.agh.tw.knapp.lab7.activeobject;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class SimpleFuture<T> implements Future<T> {
    private final CountDownLatch latch = new CountDownLatch(1);
    private T value;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return latch.getCount() == 0;
    }

    @Override
    public T get() throws InterruptedException {
        latch.await();
        return value;
    }

    @Override
    public T get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, TimeoutException {
        if (latch.await(timeout, unit)) {
            return value;
        } else {
            throw new TimeoutException();
        }
    }

    // Calling this more than once doesn't make sense, and won't
    // work properly in this implementation. so: don't.
    // Visibility: package private
    void put(Object result) {
        value = (T) result;
        latch.countDown();
    }
}