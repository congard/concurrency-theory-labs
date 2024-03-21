package pl.edu.agh.tw.knapp.lab7.demo;

import pl.edu.agh.tw.knapp.lab7.activeobject.buffer.BufferProxy;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Consumer extends Worker<Integer> {
    public Consumer(int delayMinMs, int delayMaxMs, int timeoutMs, int iterCount, BufferProxy<Integer> bufferProxy) {
        super(delayMinMs, delayMaxMs, timeoutMs, iterCount, bufferProxy);
    }

    @Override
    protected boolean onIter(int iteration) {
        try {
            var element = bufferProxy.pop().get(timeoutMs, TimeUnit.MILLISECONDS);
            log("Popped: " + element);
            return true;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            log("pop timed out, iteration=" + iteration);
            return false;
        }
    }
}
