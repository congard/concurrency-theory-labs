package pl.edu.agh.tw.knapp.lab6.worker.factory;

import pl.edu.agh.tw.knapp.lab6.worker.Writer;

public interface WriterFactory<T> extends WorkerFactory<T> {
    @Override
    Writer<T> get(int iteration);
}
