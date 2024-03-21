package pl.edu.agh.tw.knapp.lab4;

import java.util.stream.IntStream;

public class Producer extends WorkerThread<Integer> {

    public Producer(Buffer<Integer> buff, long delayMinMs, long delayMaxMs, int iterCount, int m) {
        super(buff, delayMinMs, delayMaxMs, iterCount, m);
    }

    public Producer(Buffer<Integer> buff) {
        super(buff);
    }

    @Override
    public void run() {
        log("Producer started");

        Box<Integer> counter = new Box<>((int) (getId() * Math.pow(10, (int) Math.log10(getIterCount()) + 1)));

        iterate(i -> {
            int counterVal = counter.getValue();
            int m = getRandomizedM();

            var elements = IntStream.range(counterVal, counterVal + m).boxed().toList();

            counter.setValue(counterVal + m);

            if (!buff.put(elements)) {
                log("Buffer#put error, iter " + i);
                return false;
            }

            return true;
        });
    }
}
