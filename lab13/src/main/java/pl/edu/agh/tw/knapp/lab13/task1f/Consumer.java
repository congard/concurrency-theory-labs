package pl.edu.agh.tw.knapp.lab13.task1f;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.ChannelInputInt;
import org.jcsp.lang.ChannelOutputInt;

/**
 * Consumer class: reads integers from input channel, displays them,
 * then terminates when a negative value is read.
 */
public class Consumer implements CSProcess {
    private final ChannelInputInt in;
    private final ChannelOutputInt req;

    public Consumer(ChannelOutputInt req, ChannelInputInt in) {
        this.req = req;
        this.in = in;
    }

    @Override
    public void run() {
        while (true) {
            req.write(0);

            int item = in.read();

            if (item < 0)
                break;

            System.out.println(item);
        }

        System.out.println("Consumer ended.");
    }
}
