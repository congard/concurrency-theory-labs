package pl.edu.agh.tw.knapp.lab6.finegrained;

import pl.edu.agh.tw.knapp.lab6.Database;

import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.function.Function;

public class FineGrainedDatabase<T> implements Database<T> {
    private final Node head = new Node();

    public FineGrainedDatabase() {
        // empty
    }

    @Override
    public <E extends T> boolean contains(E element) {
        var isContains = new Box<>(false);

        forEach(value -> {
            if (isEqual(value, element)) {
                isContains.setValue(true);
                return false;
            }

            return true;
        });

        return isContains.getValue();
    }

    @Override
    public <E extends T> boolean remove(E element) {
        try {
            boolean isRemoved = false;

            Node prev = head;
            prev.writeLock();

            while (!isRemoved && prev.next != null) {
                Node curr = prev.next;
                curr.writeLock();

                if (isEqual(curr.value, element)) {
                    prev.next = curr.next;
                    curr.writeUnlock();
                    curr = prev.next;

                    if (curr != null)
                        curr.writeLock();

                    isRemoved = true;
                }

                prev.writeUnlock();
                prev = curr;
            }

            if (prev != null)
                prev.writeUnlock();

            return isRemoved;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * If `o1` and `o2` are numbers, compares them as numbers.
     * Otherwise, just compares references.
     * @param o1 The first Object to compare.
     * @param o2 The second Object to compare.
     * @return `true` is equal, `false` otherwise.
     */
    private boolean isEqual(Object o1, Object o2) {
        if (o1 instanceof Number n1 && o2 instanceof Number n2)
            return n1.equals(n2);
        return o1 == o2;
    }

    @Override
    public <E extends T> boolean add(E element) {
        try {
            head.writeLock();
            head.next = new Node(element, head.next);
            head.writeUnlock();
            return true;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void forEach(Consumer<? super T> consumer) {
        forEach(value -> {
            consumer.accept(value);
            return true;
        });
    }

    private void forEach(Function<? super T, Boolean> func) {
        try {
            Node prev = head;
            prev.readLock();

            while (prev.next != null) {
                Node next = prev.next;
                next.readLock();

                boolean isBreak = !func.apply(next.value);

                prev.readUnlock();
                prev = next;

                if (isBreak) {
                    break;
                }
            }

            prev.readUnlock();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private class Node {
        private final Semaphore resource = new Semaphore(1); // controls access (read/write) to the resource
        private final Semaphore readCountLock = new Semaphore(1); // for syncing changes to shared variable readcount
        private final Semaphore serviceQueue = new Semaphore(1, true); // FAIRNESS: preserves ordering of requests

        private int readCount = 0;

        private Node next;
        private final T value;

        private Node(T value, Node next) {
            this.value = value;
            this.next = next;
        }

        private Node() {
            this(null, null);
        }

        private void readLock() throws InterruptedException {
            serviceQueue.acquire();     // wait in line to be serviced
            readCountLock.acquire();    // request exclusive access to readCount

            readCount++; // update count of active readers

            if (readCount == 1)     // if I am the first reader
                resource.acquire(); // request resource access for readers (writers blocked)

            serviceQueue.release();     // let next in line be serviced
            readCountLock.release();    // release access to readCount
        }

        private void readUnlock() throws InterruptedException {
            readCountLock.acquire(); // request exclusive access to readCount

            readCount--; // update count of active readers

            if (readCount == 0)     // if there are no readers left
                resource.release(); // release resource access for all

            readCountLock.release(); // release access to readCount
        }

        private void writeLock() throws InterruptedException {
            serviceQueue.acquire(); // wait in line to be serviced
            resource.acquire();     // request exclusive access to resource
            serviceQueue.release(); // let next in line be serviced
        }

        private void writeUnlock() {
            resource.release();      // release resource access for next reader/writer
        }
    }
}
