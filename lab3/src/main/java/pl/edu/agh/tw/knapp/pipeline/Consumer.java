package pl.edu.agh.tw.knapp.pipeline;

import pl.edu.agh.tw.knapp.Buffer;
import pl.edu.agh.tw.knapp.RandomSleeper;
import pl.edu.agh.tw.knapp.WorkerThread;

import java.util.function.Function;

public class Consumer extends WorkerThread<Box<Integer>> {
    private final Pipe<Box<Integer>> pipe = new Pipe<>() {
        @Override
        public void close() {
            // nothing
        }

        @Override
        public void submitAsync(Box<Integer> value) {
            submitValue(value, Pipe::submitAsync);
        }

        @Override
        protected Box<Integer> onSubmit(Box<Integer> value) {
            return value;
        }
    };

    public Consumer(Buffer<Box<Integer>> buff, long delayMinMs, long delayMaxMs) {
        super(buff, delayMinMs, delayMaxMs);
    }

    public Consumer(Buffer<Box<Integer>> buff) {
        super(buff);
    }

    public Pipe<Box<Integer>> pipe() {
        return this.pipe;
    }

    @Override
    public void run() {
        log("Started");

        while (true) {
            try {
                randomDelay();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            var value = buff.get();

            if (value.isPresent()) {
                pipe().submitAsync(value.get());
            } else {
                log("Buffer#get: end reached");
                break;
            }
        }

        log("Done");
    }

    public static Pipe<Box<Integer>> newPipe(
            Function<Box<Integer>, Box<Integer>> action,
            long delayMinMs, long delayMaxMs
    ) {
        return new ThreadedPipe<>() {
            private final RandomSleeper randomSleeper = new RandomSleeper(delayMinMs, delayMaxMs);

            @Override
            protected Box<Integer> onSubmit(Box<Integer> value) {
                try {
                    randomSleeper.sleep();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                return action.apply(value);
            }
        };
    }
}
