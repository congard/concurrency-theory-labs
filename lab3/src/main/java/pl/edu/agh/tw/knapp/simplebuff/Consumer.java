package pl.edu.agh.tw.knapp.simplebuff;

import pl.edu.agh.tw.knapp.Buffer;
import pl.edu.agh.tw.knapp.WorkerThread;

public class Consumer extends WorkerThread<Integer> {

    public Consumer(Buffer<Integer> buff, long delayMinMs, long delayMaxMs) {
        super(buff, delayMinMs, delayMaxMs);
    }

    public Consumer(Buffer<Integer> buff) {
        super(buff);
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; ++i) {
            try {
                randomDelay();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            var value = buff.get();

            if (value.isPresent()) {
                log(value.get());
            } else {
                log("Buffer#get: end reached");
                break;
            }
        }
    }
}
