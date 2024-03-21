package pl.edu.agh.tw.knapp.lab13.task1f;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Channel;
import org.jcsp.lang.One2OneChannelInt;
import org.jcsp.lang.Parallel;

/**
 * Main program class for Producer/Consumer example.
 * Sets up channels, creates processes then
 * executes them in parallel, using JCSP.
 */
public final class Task1fMain {
    public static void main(String[] args) {
        // Create channel objects
        One2OneChannelInt[] prodChan = {Channel.one2oneInt(), Channel.one2oneInt()};
        One2OneChannelInt[] consReq = {Channel.one2oneInt(), Channel.one2oneInt()};
        One2OneChannelInt[] consChan = {Channel.one2oneInt(), Channel.one2oneInt()};

        // Create parallel construct
        CSProcess[] procList = {
            new Producer(prodChan[0].out(), 0),
            new Producer(prodChan[1].out(), 100),
            new Buffer(prodChan, consReq, consChan),
            new Consumer(consReq[0].out(), consChan[0].in()),
            new Consumer(consReq[1].out(), consChan[1].in())
        };

        Parallel par = new Parallel(procList);
        par.run();
    }
}