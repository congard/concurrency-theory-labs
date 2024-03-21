package pl.edu.agh.tw.knapp.lab6;

import pl.edu.agh.tw.knapp.lab6.worker.Reader;
import pl.edu.agh.tw.knapp.lab6.worker.Writer;

public abstract class DatabaseDriver<T> {
    private final static Logger logger = Logger.getInstance();

    public abstract boolean read(Reader<T> reader);
    public abstract boolean write(Writer<T> writer);

    protected void logException(Exception e) {
        logger.log(getClass().getSimpleName(), "Exception: " + e.getMessage() + "\n" + e);
    }
}
