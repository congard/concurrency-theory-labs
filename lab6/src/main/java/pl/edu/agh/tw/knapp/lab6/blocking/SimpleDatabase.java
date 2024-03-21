package pl.edu.agh.tw.knapp.lab6.blocking;

import pl.edu.agh.tw.knapp.lab6.Database;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class SimpleDatabase<T> implements Database<T> {
    private final List<T> data = new LinkedList<>();

    @Override
    public <E extends T> boolean contains(E element) {
        return data.contains(element);
    }

    @Override
    public <E extends T> boolean remove(E element) {
        return data.remove(element);
    }

    @Override
    public <E extends T> boolean add(E element) {
        return data.add(element);
    }

    @Override
    public void forEach(Consumer<? super T> consumer) {
        data.forEach(consumer);
    }
}
