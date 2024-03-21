package pl.edu.agh.tw.knapp.lab6.blocking;

import pl.edu.agh.tw.knapp.lab6.Database;
import pl.edu.agh.tw.knapp.lab6.DatabaseDriver;
import pl.edu.agh.tw.knapp.lab6.worker.Reader;
import pl.edu.agh.tw.knapp.lab6.worker.Writer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingDatabaseCondDriver<T> extends DatabaseDriver<T> {
    private final Database<T> database = new SimpleDatabase<>();

    private final Lock lock = new ReentrantLock(true);
    private final Condition cond = lock.newCondition();

    int readCount = 0;

    @Override
    public boolean read(Reader<T> reader) {
        lock.lock();

        try {
            while (readCount < 0)
                cond.await();
            ++readCount;
        } catch (InterruptedException e) {
            logException(e);
            return false;
        } finally {
            lock.unlock();
        }

        // critical section: read
        reader.read(database);

        lock.lock();

        --readCount;

        if (readCount == 0)
            cond.signalAll();

        lock.unlock();

        return true;
    }

    @Override
    public boolean write(Writer<T> writer) {
        lock.lock();

        try {
            while (readCount != 0)
                cond.await();
            readCount = -1;
        } catch (InterruptedException e) {
            logException(e);
            return false;
        } finally {
            lock.unlock();
        }

        // critical section: write
        writer.write(database);

        lock.lock();
        readCount = 0;
        cond.signalAll();
        lock.unlock();

        return true;
    }
}
