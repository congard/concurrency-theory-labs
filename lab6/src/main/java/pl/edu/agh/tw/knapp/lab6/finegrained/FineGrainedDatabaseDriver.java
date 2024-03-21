package pl.edu.agh.tw.knapp.lab6.finegrained;

import pl.edu.agh.tw.knapp.lab6.Database;
import pl.edu.agh.tw.knapp.lab6.DatabaseDriver;
import pl.edu.agh.tw.knapp.lab6.worker.Reader;
import pl.edu.agh.tw.knapp.lab6.worker.Writer;

public class FineGrainedDatabaseDriver<T> extends DatabaseDriver<T> {
    private final Database<T> database = new FineGrainedDatabase<>();

    @Override
    public boolean read(Reader<T> reader) {
        reader.read(database);
        return true;
    }

    @Override
    public boolean write(Writer<T> writer) {
        writer.write(database);
        return true;
    }
}
