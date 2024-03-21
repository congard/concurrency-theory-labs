package pl.edu.agh.tw.knapp.lab4;

import java.util.List;

public interface Buffer<T> {
    /**
     * Puts the specified values to the buffer
     * @param values The values to put
     * @return `true` if success, `false` otherwise
     */
    boolean put(List<T> values);

    /**
     * Returns the specified amount of elements from the buffer
     * @param amount The amount of elements to return
     * @return The elements from the buffer
     */
    List<T> get(int amount);

    /**
     * Returns the capacity, i.e. the maximum count of elements
     * that the buffer can hold
     * @return The buffer's capacity
     */
    int capacity();
}