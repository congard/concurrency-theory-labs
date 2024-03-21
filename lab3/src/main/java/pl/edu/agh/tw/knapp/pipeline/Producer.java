package pl.edu.agh.tw.knapp.pipeline;

import pl.edu.agh.tw.knapp.Buffer;
import pl.edu.agh.tw.knapp.WorkerThread;

public class Producer extends WorkerThread<Box<Integer>> {

    public Producer(Buffer<Box<Integer>> buff, long delayMinMs, long delayMaxMs) {
        super(buff, delayMinMs, delayMaxMs);
    }

    public Producer(Buffer<Box<Integer>> buff) {
        super(buff);
    }

    @Override
    public void run() {
        log("Started");

        for (int i = 0; i < 100; ++i) {
            try {
                randomDelay();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (!buff.put(new Box<>(i))) {
                log("Buffer#put error");
                break;
            }
        }

        log("Done");
    }
}
