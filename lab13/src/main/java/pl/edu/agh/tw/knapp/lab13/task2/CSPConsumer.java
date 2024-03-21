package pl.edu.agh.tw.knapp.lab13.task2;

import org.jcsp.lang.CSProcess;

import java.util.function.Supplier;

public class CSPConsumer implements CSProcess {
    private static final Logger logger = Logger.getInstance();

    private final Supplier<Portion> supplier;
    private final Runnable onFinishListener;
    private final int maxIter;

    public CSPConsumer(Supplier<Portion> supplier, Runnable onFinishListener, int maxIter) {
        this.supplier = supplier;
        this.onFinishListener = onFinishListener;
        this.maxIter = maxIter;
    }

    @Override
    public void run() {
        for (int i = 0; i < maxIter; ++i) {
            var p = supplier.get();
            logger.log(this, "Consumed: " + p);
        }

        onFinishListener.run();
    }
}
