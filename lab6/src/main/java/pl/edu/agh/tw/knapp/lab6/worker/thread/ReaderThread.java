package pl.edu.agh.tw.knapp.lab6.worker.thread;

import pl.edu.agh.tw.knapp.lab6.DatabaseDriver;
import pl.edu.agh.tw.knapp.lab6.worker.Reader;
import pl.edu.agh.tw.knapp.lab6.worker.factory.ReaderFactory;

public class ReaderThread<T> extends WorkerThread<T> {
    public ReaderThread(ReaderFactory<T> factory, DatabaseDriver<T> driver,
                        int delayMinMs, int delayMaxMs, int iterCount)
    {
        super(factory, driver, delayMinMs, delayMaxMs, iterCount);
    }

    @Override
    protected void onIter(int i) {
        driver.read((Reader<T>) factory.get(i));
    }
}
