package pl.edu.agh.tw.knapp.lab13.task1f;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.ChannelOutputInt;

/**
 * Producer class: produces 100 random integers and sends them on
 * output channel, then sends -1 and terminates.
 * The random integers are in a given range [start...start+100)
 */
public class Producer implements CSProcess {
    private final ChannelOutputInt channel;
    private final int start;

    public Producer(ChannelOutputInt out, int start) {
        channel = out;
        this.start = start;
    }

    @Override
    public void run() {
        for (int k = 0; k < 100; k++) {
            int item = (int) (Math.random() * 100) + 1 + start;
            channel.write(item);
        }

        channel.write(-1);

        System.out.println("Producer" + start + " ended.");
    }
}