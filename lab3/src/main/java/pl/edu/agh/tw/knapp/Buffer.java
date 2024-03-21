package pl.edu.agh.tw.knapp;

import java.util.Optional;

public interface Buffer<T> {
    boolean put(T val);
    Optional<T> get();
}