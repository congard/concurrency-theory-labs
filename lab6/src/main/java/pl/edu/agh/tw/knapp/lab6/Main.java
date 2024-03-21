package pl.edu.agh.tw.knapp.lab6;

import pl.edu.agh.tw.knapp.lab6.blocking.BlockingDatabaseCondDriver;
import pl.edu.agh.tw.knapp.lab6.blocking.BlockingDatabaseDriver;
import pl.edu.agh.tw.knapp.lab6.finegrained.FineGrainedDatabaseDriver;
import pl.edu.agh.tw.knapp.lab6.worker.Reader;
import pl.edu.agh.tw.knapp.lab6.worker.Writer;
import pl.edu.agh.tw.knapp.lab6.worker.factory.ReaderFactory;
import pl.edu.agh.tw.knapp.lab6.worker.factory.WriterFactory;
import pl.edu.agh.tw.knapp.lab6.worker.thread.ReaderThread;
import pl.edu.agh.tw.knapp.lab6.worker.thread.WriterThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Main {
    private final static Logger logger = Logger.getInstance();

    private record Params(
            int delayMin,
            int delayMax,
            int iterCount,
            int writerCount,
            int readerCount
    ) {}

    private enum DriverType {
        BLOCKING,
        BLOCKING_COND,
        FINE_GRAINED
    }

    /**
     * The application's entry point
     * @param args The arguments list:<br>
     *             args[0]: The minimum delay, ms, `int`<br>
     *             args[1]: The maximum delay, ms, `int`<br>
     *             args[2]: The number of iterations, `int`<br>
     *             args[3]: The number of writers, `int`<br>
     *             args[4]: The number of readers, `int`<br>
     *             args[5]: The database driver type: 'blocking', 'blocking_cond',
     *                      'fine_grained'
     *             args[6]: Whether mute the Logger or not, `boolean`<br>
     */
    public static void main(String[] args) {
        var params = new Params(0, 100, 100, 10, 100);
        var driverType = DriverType.FINE_GRAINED;

        if (args.length != 0) {
            if (args.length != 7)
                throw new IllegalArgumentException("expected 7 arguments, got " + args.length);

            params = new Params(
                    Integer.parseInt(args[0]),
                    Integer.parseInt(args[1]),
                    Integer.parseInt(args[2]),
                    Integer.parseInt(args[3]),
                    Integer.parseInt(args[4]));

            driverType = DriverType.valueOf(args[5].toUpperCase());

            if (Boolean.parseBoolean(args[6])) {
                logger.mute();
            }
        }

        var driver = switch (driverType) {
            case BLOCKING -> new BlockingDatabaseDriver<Integer>();
            case BLOCKING_COND -> new BlockingDatabaseCondDriver<Integer>();
            case FINE_GRAINED -> new FineGrainedDatabaseDriver<Integer>();
        };

        Params finalParams = params;

        long time = measureTime(() -> {
            try {
                demo(finalParams, driver);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("time=" + time);
    }

    private static void demo(Params p, DatabaseDriver<Integer> driver) throws InterruptedException {
        var writers = IntStream.range(0, p.writerCount)
                .mapToObj(i -> mkWriter(i, p, driver))
                .toList();

        var readers = IntStream.range(0, p.readerCount)
                .mapToObj(i -> mkReader(i, p, driver))
                .toList();

        writers.forEach(Thread::start);
        readers.forEach(Thread::start);

        joinAll(writers);
        joinAll(readers);
    }

    private static ReaderThread<Integer> mkReader(int index, Params p, DatabaseDriver<Integer> driver) {
        return new ReaderThread<>(new MyReaderFactory(index), driver, p.delayMin, p.delayMax, p.iterCount);
    }

    private static WriterThread<Integer> mkWriter(int index, Params p, DatabaseDriver<Integer> driver) {
        return new WriterThread<>(new MyWriterFactory(index), driver, p.delayMin, p.delayMax, p.iterCount);
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

    private record MyReaderFactory(int index) implements ReaderFactory<Integer> {

        @Override
        public Reader<Integer> get(int iteration) {
            return db -> {
                var data = new ArrayList<Integer>();
                db.forEach(data::add);
                logger.log(getClass().getSimpleName() + " " + index,
                        String.format("i=%s, data=%s", iteration, data));
            };
        }
    }

    private record MyWriterFactory(int index) implements WriterFactory<Integer> {
        private final static AtomicInteger addValue = new AtomicInteger(0);
        private final static AtomicInteger removeValue = new AtomicInteger(0);

        @Override
        public Writer<Integer> get(int iteration) {
            return db -> {
                if (iteration % 3 == 2) {
                    var value = removeValue.getAndIncrement();
                    var isRemoved = db.remove(value);
                    var msg = String.format("i=%s, value=%s, isRemoved=%s",
                            iteration, value, isRemoved);
                    logger.log(getClass().getSimpleName() + " " + index, msg);
                } else {
                    var value = addValue.getAndIncrement();
                    var isAdded = db.add(value);

                    logger.log(getClass().getSimpleName() + " " + index,
                            String.format("i=%s, value=%s, isAdded=%s", iteration, value, isAdded));
                }
            };
        }
    }
}