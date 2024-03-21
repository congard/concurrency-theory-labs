package pl.edu.agh.tw.knapp.lab7.activeobject.buffer;

import pl.edu.agh.tw.knapp.lab7.activeobject.MethodRequest;
import pl.edu.agh.tw.knapp.lab7.activeobject.Scheduler;

import java.util.concurrent.Future;
import java.util.function.Supplier;

public class BufferProxy<T> {
    private final Buffer<T> buffer;
    private final Scheduler scheduler;

    public BufferProxy(int capacity, Scheduler scheduler) {
        buffer = new Buffer<>(capacity);
        this.scheduler = scheduler;
    }

    public Future<Boolean> put(T element) {
        return scheduler.enqueue(mkMethodRequest(() -> buffer.put(element), buffer::isNotFull));
    }

    public Future<T> pop() {
        return scheduler.enqueue(mkMethodRequest(buffer::pop, buffer::isNotEmpty));
    }

    public Future<Integer> size() {
        return scheduler.enqueue(mkMethodRequest(buffer::size, () -> true));
    }

    public Future<Integer> capacity() {
        return scheduler.enqueue(mkMethodRequest(buffer::capacity, () -> true));
    }

    public Future<Boolean> isEmpty() {
        return scheduler.enqueue(mkMethodRequest(buffer::isEmpty, () -> true));
    }

    public Future<Boolean> isNotEmpty() {
        return scheduler.enqueue(mkMethodRequest(buffer::isNotEmpty, () -> true));
    }

    public Future<Boolean> isFull() {
        return scheduler.enqueue(mkMethodRequest(buffer::isFull, () -> true));
    }

    public Future<Boolean> isNotFull() {
        return scheduler.enqueue(mkMethodRequest(buffer::isNotFull, () -> true));
    }

    private static <V> MethodRequest<V> mkMethodRequest(Supplier<V> call, Supplier<Boolean> guard) {
        return new MethodRequest<>() {
            @Override
            public V call() {
                return call.get();
            }

            @Override
            public boolean guard() {
                return guard.get();
            }
        };
    }
}
