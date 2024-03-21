package pl.edu.agh.tw.knapp.simplebuff;

import pl.edu.agh.tw.knapp.Buffer;
import pl.edu.agh.tw.knapp.WorkerThread;

public class Producer extends WorkerThread<Integer> {

    public Producer(Buffer<Integer> buff, long delayMinMs, long delayMaxMs) {
        super(buff, delayMinMs, delayMaxMs);
    }

    public Producer(Buffer<Integer> buff) {
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

            if (!buff.put(i)) {
                log("Buffer#put error");
                break;
            }
        }
    }
}
