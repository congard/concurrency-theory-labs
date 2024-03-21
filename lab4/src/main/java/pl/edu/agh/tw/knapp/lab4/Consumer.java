package pl.edu.agh.tw.knapp.lab4;

public class Consumer extends WorkerThread<Integer> {

    public Consumer(Buffer<Integer> buff, long delayMinMs, long delayMaxMs, int iterCount, int m) {
        super(buff, delayMinMs, delayMaxMs, iterCount, m);
    }

    public Consumer(Buffer<Integer> buff) {
        super(buff);
    }

    @Override
    public void run() {
        iterate(i -> {
            var values = buff.get(getRandomizedM());

            if (!values.isEmpty()) {
                log(values);
            } else {
                log("Buffer#get: end reached, iter " + i);
            }

            return !values.isEmpty();
        });
    }
}
