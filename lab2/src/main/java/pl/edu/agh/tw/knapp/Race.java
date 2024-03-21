package pl.edu.agh.tw.knapp;

public class Race {
    public static void main(String[] args) throws InterruptedException {
        final int operationCount = 10_000;

        var incThread = new IThread(operationCount);
        var decThread = new DThread(operationCount);

        var counter = new SynchronizedCounter(0, new Semaphore(1));

        incThread.setCounter(counter);
        decThread.setCounter(counter);

        incThread.start();
        decThread.start();

        incThread.join();
        decThread.join();

        System.out.println("stan=" + counter.value());
    }
}