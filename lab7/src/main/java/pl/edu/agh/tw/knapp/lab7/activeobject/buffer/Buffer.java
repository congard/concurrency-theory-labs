package pl.edu.agh.tw.knapp.lab7.activeobject.buffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Buffer<T> {
    private final List<T> buffer;
    private int bufferPos = 0;
    private int bufferActualSize = 0;

    public Buffer(int capacity) {
        buffer = new ArrayList<>(Collections.nCopies(capacity, null));
    }

    public boolean put(T element) {
        if (isFull())
            return false;

        var index = (bufferPos + bufferActualSize) % buffer.size();
        ++bufferActualSize;
        buffer.set(index, element);

        return true;
    }

    public T pop() {
        if (isEmpty())
            return null;

        var result = buffer.get(bufferPos);
        bufferPos = (bufferPos + 1) % buffer.size();
        --bufferActualSize;

        return result;
    }

    public int size() {
        return bufferActualSize;
    }

    public int capacity() {
        return buffer.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public boolean isFull() {
        return size() == capacity();
    }

    public boolean isNotFull() {
        return !isFull();
    }
}
