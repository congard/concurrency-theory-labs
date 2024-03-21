package pl.edu.agh.tw.knapp.lab6.worker.factory;

import pl.edu.agh.tw.knapp.lab6.worker.Worker;

public interface WorkerFactory<T> {
    Worker<T> get(int iteration);
}
