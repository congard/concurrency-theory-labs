package pl.edu.agh.tw.knapp.lab13.task1;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Channel;
import org.jcsp.lang.One2OneChannelInt;
import org.jcsp.lang.Parallel;

public class Task1Main {
    public static void main(String[] args) {
        // Create channel object
        One2OneChannelInt channel = Channel.one2oneInt();

        // Create and run with a list of parallel constructs
        CSProcess[] procList = {
            new Producer(channel.out()),
            new Consumer(channel.in())
        };

        // Processes
        Parallel par = new Parallel(procList); // PAR construct
        par.run(); // Execute processes in parallel
    }
}