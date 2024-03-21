package pl.edu.agh.tw.knapp.lab6.worker.thread;

import pl.edu.agh.tw.knapp.lab6.DatabaseDriver;
import pl.edu.agh.tw.knapp.lab6.worker.Writer;
import pl.edu.agh.tw.knapp.lab6.worker.factory.WriterFactory;

public class WriterThread<T> extends WorkerThread<T> {
    public WriterThread(WriterFactory<T> factory, DatabaseDriver<T> driver,
                        int delayMinMs, int delayMaxMs, int iterCount)
    {
        super(factory, driver, delayMinMs, delayMaxMs, iterCount);
    }

    @Override
    protected void onIter(int i) {
        driver.write((Writer<T>) factory.get(i));
    }
}
