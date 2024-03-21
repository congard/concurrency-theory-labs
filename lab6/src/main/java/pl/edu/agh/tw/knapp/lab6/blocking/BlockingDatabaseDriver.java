package pl.edu.agh.tw.knapp.lab6.blocking;

import pl.edu.agh.tw.knapp.lab6.*;
import pl.edu.agh.tw.knapp.lab6.worker.Reader;
import pl.edu.agh.tw.knapp.lab6.worker.Writer;

import java.util.concurrent.Semaphore;

public class BlockingDatabaseDriver<T> extends DatabaseDriver<T> {
    private final Database<T> database = new SimpleDatabase<>();

    private final Semaphore resource = new Semaphore(1); // controls access (read/write) to the resource
    private final Semaphore readCountLock = new Semaphore(1); // for syncing changes to shared variable readcount
    private final Semaphore serviceQueue = new Semaphore(1, true); // FAIRNESS: preserves ordering of requests

    private int readCount = 0;

    @Override
    public boolean read(Reader<T> reader) {
        try {
            readImpl(reader);
            return true;
        } catch (InterruptedException e) {
            logException(e);
            return false;
        }
    }

    @Override
    public boolean write(Writer<T> writer) {
        try {
            writeImpl(writer);
            return true;
        } catch (InterruptedException e) {
            logException(e);
            return false;
        }
    }

    private void readImpl(Reader<T> reader) throws InterruptedException {
        serviceQueue.acquire();     // wait in line to be serviced
        readCountLock.acquire();    // request exclusive access to readCount

        readCount++; // update count of active readers

        if (readCount == 1)     // if I am the first reader
            resource.acquire(); // request resource access for readers (writers blocked)

        serviceQueue.release();     // let next in line be serviced
        readCountLock.release();    // release access to readCount

        // critical section: perform reading
        reader.read(database);

        readCountLock.acquire(); // request exclusive access to readCount

        readCount--; // update count of active readers

        if (readCount == 0)     // if there are no readers left
            resource.release(); // release resource access for all

        readCountLock.release(); // release access to readCount
    }

    private void writeImpl(Writer<T> writer) throws InterruptedException {
        serviceQueue.acquire(); // wait in line to be serviced
        resource.acquire();     // request exclusive access to resource
        serviceQueue.release(); // let next in line be serviced

        // critical section: perform writing
        writer.write(database);

        resource.release();      // release resource access for next reader/writer
    }
}
