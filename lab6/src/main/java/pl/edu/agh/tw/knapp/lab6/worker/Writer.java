package pl.edu.agh.tw.knapp.lab6.worker;

import pl.edu.agh.tw.knapp.lab6.Database;

public interface Writer<T> extends Worker<T> {
    default void write(Database<T> db) {
        work(db);
    }
}
