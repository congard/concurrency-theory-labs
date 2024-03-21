package pl.edu.agh.tw.knapp.lab7.activeobject;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Consumer;

public class MethodRequestQueue<T extends MethodRequest<?>> {
    private Queue<T> waitQueue = new ArrayDeque<>();
    private Queue<T> currentQueue = new ArrayDeque<>();

    public void put(T element) {
        currentQueue.add(element);
    }

    public void forEachReady(Consumer<? super T> consumer) {
        while (!currentQueue.isEmpty()) {
            var element = currentQueue.poll();

            if (element.guard()) {
                consumer.accept(element);

                // try to execute something from the wait queue
                for (int i = 0; i < waitQueue.size(); i++) {
                    var waiting = waitQueue.poll();

                    if (waiting.guard()) {
                        consumer.accept(waiting);
                    } else {
                        waitQueue.add(waiting);
                    }
                }
            } else {
                waitQueue.add(element);
            }
        }

        // swap, currentQueue is empty now,
        // waitQueue may be not empty
        var tmp = waitQueue;
        waitQueue = currentQueue;
        currentQueue = tmp;
    }

    public int currentSize() {
        return currentQueue.size();
    }

    public int waitSize() {
        return waitQueue.size();
    }

    public int size() {
        return currentSize() + waitSize();
    }
}
