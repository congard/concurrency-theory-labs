package pl.edu.agh.tw.knapp.simplebuff;

import java.util.List;
import java.util.stream.Stream;

import pl.edu.agh.tw.knapp.Buffer;

public class Main {
    public static void main(String[] args) throws Exception {
        monitorDemo();
        semaphoreDemo();
    }

    private static void semaphoreDemo() throws InterruptedException {
        final long timeoutMs = 1000L;
        many2many(1, 1, new SemaphoreBuffer<>(timeoutMs));
        many2many(1, 10, new SemaphoreBuffer<>(timeoutMs));
        many2many(10, 1, new SemaphoreBuffer<>(timeoutMs));
        many2many(100, 100, new SemaphoreBuffer<>(timeoutMs));
    }

    private static void monitorDemo() throws InterruptedException {
        final long timeoutMs = 1000L;
        many2many(1, 1, new MonitorBuffer<>(timeoutMs));
        many2many(1, 10, new MonitorBuffer<>(timeoutMs));
        many2many(10, 1, new MonitorBuffer<>(timeoutMs));
        many2many(100, 100, new MonitorBuffer<>(timeoutMs));
    }

    /**
     * Many producers, many consumers
     * @param p Producer count
     * @param c Consumer count
     * @param buffer The buffer to use
     */
    private static void many2many(int p, int c, Buffer<Integer> buffer) throws InterruptedException {
        System.out.printf("******* producers = %s, consumers = %s, buffer: %s *******\n",
                p, c, buffer.getClass().getSimpleName());

        var consumers = Stream.generate(() -> new Consumer(buffer, 0, 100))
                .limit(c)
                .toList();

        var producers = Stream.generate(() -> new Producer(buffer, 0, 100))
                .limit(p)
                .toList();

        consumers.forEach(Thread::start);
        producers.forEach(Thread::start);

        joinAll(producers);
        joinAll(consumers);
    }

    private static void joinAll(List<? extends Thread> threads) throws InterruptedException {
        for (var thread : threads) {
            thread.join();
        }
    }
}