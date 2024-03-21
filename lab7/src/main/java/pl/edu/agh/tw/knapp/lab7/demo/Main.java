package pl.edu.agh.tw.knapp.lab7.demo;

import pl.edu.agh.tw.knapp.lab7.activeobject.Scheduler;
import pl.edu.agh.tw.knapp.lab7.activeobject.buffer.BufferProxy;

import java.util.List;
import java.util.stream.Stream;

public class Main {
    private record Params(
            int delayMinMs,
            int delayMaxMs,
            int timeoutMs,
            int iterCount,
            int producerCount,
            int consumerCount,
            int bufferCapacity
    ) {}

    /**
     * The application's entry point
     * @param args The arguments list:<br>
     *             args[0]: The minimum delay, ms, `int`<br>
     *             args[1]: The maximum delay, ms, `int`<br>
     *             args[2]: The wait timeout, ms, `int`<br>
     *             args[3]: The number of iterations, `int`<br>
     *             args[4]: The number of producers, `int`<br>
     *             args[5]: The number of consumers, `int`<br>
     *             args[6]: The buffer capacity, `int`<br>
     */
    public static void main(String[] args) throws InterruptedException {
        var params = new Params(0, 0, 1000, 100, 10, 10, 100);

        if (args.length != 0) {
            if (args.length != 7)
                throw new IllegalArgumentException("expected 7 arguments, got " + args.length);

            params = new Params(
                    Integer.parseInt(args[0]),
                    Integer.parseInt(args[1]),
                    Integer.parseInt(args[2]),
                    Integer.parseInt(args[3]),
                    Integer.parseInt(args[4]),
                    Integer.parseInt(args[5]),
                    Integer.parseInt(args[6]));
        }

        var scheduler = new Scheduler();
        scheduler.setAutoDispatchMod(1);
        scheduler.start();

        var buffer = new BufferProxy<Integer>(params.bufferCapacity, scheduler);

        demo(params, buffer);

        scheduler.dispatch();
        scheduler.shutdown();
        scheduler.join();
    }

    private static void demo(Params p, BufferProxy<Integer> bufferProxy) throws InterruptedException {
        var producers = Stream.generate(() -> mkProducer(p, bufferProxy))
                .limit(p.producerCount)
                .toList();

        var consumers = Stream.generate(() -> mkConsumer(p, bufferProxy))
                .limit(p.consumerCount)
                .toList();

        producers.forEach(Thread::start);
        consumers.forEach(Thread::start);

        joinAll(producers);
        joinAll(consumers);
    }

    private static Producer mkProducer(Params p, BufferProxy<Integer> bufferProxy) {
        return new Producer(p.delayMinMs, p.delayMaxMs, p.timeoutMs, p.iterCount, bufferProxy);
    }

    private static Consumer mkConsumer(Params p, BufferProxy<Integer> bufferProxy) {
        return new Consumer(p.delayMinMs, p.delayMaxMs, p.timeoutMs, p.iterCount, bufferProxy);
    }

    private static void joinAll(List<? extends Thread> threads) throws InterruptedException {
        for (var thread : threads) {
            thread.join();
        }
    }
}