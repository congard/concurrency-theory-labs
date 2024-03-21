package pl.edu.agh.tw.knapp.lab5;

import pl.edu.agh.tw.knapp.lab5.naive.AdvancedNaiveArbiter;
import pl.edu.agh.tw.knapp.lab5.naive.NaiveArbiter;
import pl.edu.agh.tw.knapp.lab5.simultaneous.ForkPool;
import pl.edu.agh.tw.knapp.lab5.waiter.Waiter;

import java.util.List;
import java.util.stream.Stream;

public class Main {
    private record Params(
            int delayMin,
            int delayMax,
            int iterCount,
            int count
    ) {}

    private enum ArbiterType {
        NAIVE,
        ADVANCED_NAIVE,
        SIMULTANEOUS,
        WAITER
    }

    /**
     * The application's entry point
     * @param args The arguments list:<br>
     *             args[0]: The minimum delay, ms, `int`<br>
     *             args[1]: The maximum delay, ms, `int`<br>
     *             args[2]: The number of iterations, `int`<br>
     *             args[3]: The philosopher count, `int`<br>
     *             args[4]: Whether mute the Logger or not, `boolean`<br>
     *             args[5]: The arbiter type: 'naive', 'advanced_naive',
     *                      'simultaneous', 'waiter'
     */
    public static void main(String[] args) {
        var params = new Params(0, 0, 100, 6);
        ArbiterType arbiterType = ArbiterType.WAITER;

        if (args.length > 0) {
            if (args.length != 6)
                throw new IllegalArgumentException("expected 6 arguments, got " + args.length);

            params = new Params(
                    Integer.parseInt(args[0]),
                    Integer.parseInt(args[1]),
                    Integer.parseInt(args[2]),
                    Integer.parseInt(args[3]));

            if (Boolean.parseBoolean(args[4])) {
                Logger.getInstance().mute();
            }

            arbiterType = ArbiterType.valueOf(args[5].toUpperCase());
        }

        var arbiter = switch (arbiterType) {
            case NAIVE -> new NaiveArbiter();
            case ADVANCED_NAIVE -> new AdvancedNaiveArbiter();
            case SIMULTANEOUS -> new ForkPool();
            case WAITER -> new Waiter();
        };

        var finalParams = params;
        var time = measureTime(() -> runNoThrow(arbiter, finalParams));

        System.out.printf("time=%s\n", time);
    }

    private static void runNoThrow(Arbiter arbiter, Params params) {
        try {
            run(arbiter, params);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void run(Arbiter arbiter, Params p) throws InterruptedException {
        var philosophers = Stream.generate(() -> new Philosopher(arbiter, p.delayMin, p.delayMax, p.iterCount))
                .limit(p.count)
                .toList();

        var forks = Stream.generate(Fork::new).limit(p.count).toList();

        var table = new Table(philosophers, forks);
        arbiter.setTable(table);

        philosophers.forEach(Thread::start);
        joinAll(philosophers);
    }

    private static void joinAll(List<? extends Thread> threads) throws InterruptedException {
        for (var thread : threads) {
            thread.join();
        }
    }

    private static long measureTime(Runnable runnable) {
        long startTime = System.currentTimeMillis();
        runnable.run();
        return System.currentTimeMillis() - startTime;
    }
}