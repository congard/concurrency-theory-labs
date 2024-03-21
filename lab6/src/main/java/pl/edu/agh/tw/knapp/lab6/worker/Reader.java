package pl.edu.agh.tw.knapp.lab6.worker;

import pl.edu.agh.tw.knapp.lab6.Database;

public interface Reader<T> extends Worker<T> {
    default void read(Database<T> db) {
        work(db);
    }
}
