package pl.edu.agh.tw.knapp.lab13.task1;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.ChannelInputInt;

public class Consumer implements CSProcess {
    private final ChannelInputInt channel;

    public Consumer(ChannelInputInt in) {
        channel = in;
    }

    @Override
    public void run() {
        int item = channel.read();
        System.out.println(item);
    }
}