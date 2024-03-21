package pl.edu.agh.tw.knapp.lab4;

import java.util.List;
import java.util.stream.Stream;

public class Main {
    private static final long CONSUMER_MIN_DELAY = 0;
    private static final long CONSUMER_MAX_DELAY = 0;

    private static final long PRODUCER_MIN_DELAY = 0;
    private static final long PRODUCER_MAX_DELAY = 0;

    private static final Logger logger = Logger.getInstance();

    /**
     * The application's entry point
     * @param args The arguments list:<br>
     *             args[0]: The producer count<br>
     *             args[1]: The consumer count<br>
     *             args[2]: The iteration count (for both producers and consumers)<br>
     *             args[3]: The buffer size<br>
     *             args[4]: The buffer timeout, in ms<br>
     *             args[5]: The upper bound of element count produced by Producer / consumed by Consumer
     *                      in one iteration. If `-1`, `buffer.capacity() / 2` will be used<br>
     *             args[6]: (optional) Output params, valid values: 'disable-output'
     */
    public static void main(String[] args) throws InterruptedException {
        int p = 10;
        int c = 10;
        int iterCount = 100;
        int bufferSize = 100;
        int bufferTimeout = 1000;
        int m = -1;

        if (args.length > 0) {
            if (args.length != 6 && args.length != 7) {
                throw new IllegalArgumentException(String.format(
                        "Unexpected argument count: expected 0, 6 or 7, got %s", args.length));
            }

            p = Integer.parseInt(args[0]);
            c = Integer.parseInt(args[1]);
            iterCount = Integer.parseInt(args[2]);
            bufferSize = Integer.parseInt(args[3]);
            bufferTimeout = Integer.parseInt(args[4]);
            m = Integer.parseInt(args[5]);

            if (args.length == 7) {
                if (args[6].equals("disable-output")) {
                    logger.mute();
                } else {
                    throw new IllegalArgumentException(String.format(
                            "Illegal argument: '%s', valid arguments are: 'disable-output'", args[6]));
                }
            }
        }

        System.out.printf("time=%s\n", demo(p, c, iterCount, m, new SemaphoreBuffer<>(bufferSize, bufferTimeout)));
    }

    private static long demo(int p, int c, int iterCount, int m, Buffer<Integer> buffer) throws InterruptedException {
        logger.log(String.format("******* producers = %s, consumers = %s, buffer: %s *******\n",
                p, c, buffer.getClass().getSimpleName()));

        int validM = m == -1 ? buffer.capacity() / 2 : m;

        var consumers = Stream.generate(() -> mkConsumer(buffer, iterCount, validM))
                .limit(c)
                .toList();

        var producers = Stream.generate(() -> mkProducer(buffer, iterCount, validM))
                .limit(p)
                .toList();

        long startTime = System.currentTimeMillis();

        consumers.forEach(Thread::start);
        producers.forEach(Thread::start);

        joinAll(producers);
        joinAll(consumers);

        long elapsedTime = System.currentTimeMillis() - startTime;

        logger.log("Done in " + elapsedTime + " ms");

        return elapsedTime;
    }

    private static Consumer mkConsumer(Buffer<Integer> buffer, int iterCount, int m) {
        return new Consumer(buffer, CONSUMER_MIN_DELAY, CONSUMER_MAX_DELAY, iterCount, m);
    }

    private static Producer mkProducer(Buffer<Integer> buffer, int iterCount, int m) {
        return new Producer(buffer, PRODUCER_MIN_DELAY, PRODUCER_MAX_DELAY, iterCount, m);
    }

    private static void joinAll(List<? extends Thread> threads) throws InterruptedException {
        for (var thread : threads) {
            thread.join();
        }
    }
}