package pl.edu.agh.tw.knapp.lab6.worker;

import pl.edu.agh.tw.knapp.lab6.Database;

public interface Worker<T> {
    void work(Database<T> db);
}
