package pl.edu.agh.tw.knapp.lab13.task2;

import org.jcsp.lang.CSProcess;

import java.util.function.Consumer;

public class CSPProducer implements CSProcess {
    private static final Logger logger = Logger.getInstance();

    private final Consumer<Portion> consumer;
    private final int maxIter;

    public CSPProducer(Consumer<Portion> consumer, int maxIter) {
        this.consumer = consumer;
        this.maxIter = maxIter;
    }

    @Override
    public void run() {
        for (int i = 0; i < maxIter; ++i) {
            var p = new Portion(i);
            consumer.accept(p);
            logger.log(this, "Produced a new portion of data");
        }
    }
}