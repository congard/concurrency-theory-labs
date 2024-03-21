package pl.edu.agh.tw.knapp.lab6;

import java.util.function.Consumer;

public interface Database<T> {
    <E extends T> boolean contains(E element);
    <E extends T> boolean remove(E element);
    <E extends T> boolean add(E element);
    void forEach(Consumer<? super T> consumer);
}
