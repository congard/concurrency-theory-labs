package pl.edu.agh.tw.knapp.pipeline;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ThreadedPipe<T> extends Pipe<T> {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ThreadedPipe() {
        // empty
    }

    @Override
    public void close() {
        executorService.shutdown();
    }

    @Override
    public void submitAsync(T value) {
        executorService.submit(() -> submitValue(value, Pipe::submitAsync));
    }

    @Override
    public void submit(T value) {
        var future = executorService.submit(() -> super.submit(value));

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
