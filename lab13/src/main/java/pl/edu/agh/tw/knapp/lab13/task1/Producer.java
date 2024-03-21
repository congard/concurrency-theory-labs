package pl.edu.agh.tw.knapp.lab13.task1;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.ChannelOutputInt;

class Producer implements CSProcess {
    private final ChannelOutputInt channel;

    public Producer(ChannelOutputInt out) {
        channel = out;
    }

    @Override
    public void run() {
        int item = (int)(Math.random() * 100) + 1;
        channel.write(item);
    }
}