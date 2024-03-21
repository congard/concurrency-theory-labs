package pl.edu.agh.tw.knapp.lab7.demo;

import pl.edu.agh.tw.knapp.lab7.activeobject.buffer.BufferProxy;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class Producer extends Worker<Integer> {
    private final static AtomicInteger counter = new AtomicInteger();

    public Producer(int delayMinMs, int delayMaxMs, int timeoutMs, int iterCount, BufferProxy<Integer> bufferProxy) {
        super(delayMinMs, delayMaxMs, timeoutMs, iterCount, bufferProxy);
    }

    @Override
    protected boolean onIter(int iteration) {
        var value = counter.getAndIncrement();

        try {
            log("Putting " + value);

            boolean result = bufferProxy.put(value).get(timeoutMs, TimeUnit.MILLISECONDS);

            if (!result) {
                log("put has failed");
            } else {
                log("Putted " + value);
            }

            return result;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            log("put timed out, value=" + value);
            return false;
        }
    }
}
