package pl.edu.agh.tw.knapp.lab13.task2;

import org.jcsp.lang.*;

import java.util.concurrent.atomic.AtomicInteger;

public class Task2aMain {
    private static final int BUFFERS = 100;
    private static final int CONSUMERS = 1;
    private static final int PRODUCERS = 1;

    private static final int CONSUMER_MAX_ITER = 100;
    private static final int PRODUCER_MAX_ITER = 100;

    public static void main(String[] args) {
        // Logger.getInstance().mute();

        var timer = new Timer();

        // Array of all processes
        var processes = new CSProcess[BUFFERS + CONSUMERS + PRODUCERS];

        // Channels for interprocess communication
        Any2AnyChannel<Portion> consChannel = Channel.any2any();
        Any2AnyChannel<Portion> prodChannel = Channel.any2any();

        // Producers
        for (int i = 0; i < PRODUCERS; ++i)
            processes[i] = new CSPProducer(p -> prodChannel.out().write(p), PRODUCER_MAX_ITER);

        // Consumers
        var consumerOnFinishListener = new Runnable() {
            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public void run() {
                if (counter.incrementAndGet() == CONSUMERS) {
                    timer.end();
                    System.out.println(timer);
                    System.exit(0);
                }
            }
        };

        for (int i = 0; i < CONSUMERS; ++i) {
            processes[i + PRODUCERS] = new CSPConsumer(
                    () -> consChannel.in().read(),
                    consumerOnFinishListener,
                    CONSUMER_MAX_ITER);
        }

        // Buffers
        for (int i = 0; i < BUFFERS; i++) {
            processes[i + PRODUCERS + CONSUMERS] = new Buffer(
                    () -> prodChannel.in().read(),
                    p -> consChannel.out().write(p));
        }

        // Run in parallel
        var par = new Parallel(processes);

        timer.start();
        par.run();
    }
}
