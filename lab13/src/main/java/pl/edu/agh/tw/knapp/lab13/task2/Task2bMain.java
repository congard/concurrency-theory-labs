package pl.edu.agh.tw.knapp.lab13.task2;

import org.jcsp.lang.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Task2bMain {
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
        One2OneChannel<Portion>[] buffChannels = Channel.one2oneArray(BUFFERS - 1);
        Any2OneChannel<Portion> prodChannel = Channel.any2one();
        One2AnyChannel<Portion> consChannel = Channel.one2any();

        Consumer<Portion> prodConsumer = p -> prodChannel.out().write(p);
        Supplier<Portion> consSupplier = () -> consChannel.in().read();

        // Producers
        for (int i = 0; i < PRODUCERS; ++i)
            processes[i] = new CSPProducer(prodConsumer, PRODUCER_MAX_ITER);

        // Buffers
        processes[PRODUCERS] = new Buffer(
                () -> prodChannel.in().read(),
                p -> buffChannels[0].out().write(p));

        for (int i = 1; i < BUFFERS - 1; i++) {
            int j = i;

            processes[i + PRODUCERS] = new Buffer(
                    () -> buffChannels[j - 1].in().read(),
                    p -> buffChannels[j].out().write(p));
        }

        processes[PRODUCERS + BUFFERS - 1] = new Buffer(
                () -> buffChannels[BUFFERS - 2].in().read(),
                p -> consChannel.out().write(p));

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

        for (int i = 0; i < CONSUMERS; ++i)
            processes[i + PRODUCERS + BUFFERS] = new CSPConsumer(
                    consSupplier, consumerOnFinishListener, CONSUMER_MAX_ITER);

        // Run in parallel
        var par = new Parallel(processes);

        timer.start();
        par.run();
    }
}
