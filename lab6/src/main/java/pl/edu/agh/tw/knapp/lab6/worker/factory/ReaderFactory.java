package pl.edu.agh.tw.knapp.lab6.worker.factory;

import pl.edu.agh.tw.knapp.lab6.worker.Reader;

public interface ReaderFactory<T> extends WorkerFactory<T> {
    @Override
    Reader<T> get(int iteration);
}
