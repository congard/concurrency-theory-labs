package pl.edu.agh.tw.knapp.pipeline;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        var buffer = new SemaphoreBuffer<Box<Integer>>(100);
        var producer = new Producer(buffer, 0, 50);
        var consumer = new Consumer(buffer, 0, 100);

        PipeAction<Box<Integer>> resultAction = result -> {
            System.out.println(result);

            if (result.getValue() == 105) {
                consumer.pipe().closeAll();
            }
        };

        consumer.pipe()
                .then(newAddPipe(1))
                .then(newAddPipe(1))
                .then(newAddPipe(1))
                .then(newAddPipe(1))
                .then(newAddPipe(2))
                .action(resultAction);

        producer.start();
        consumer.start();

        consumer.join();
        producer.join();
    }

    /**
     * A simple wrapper for `Consumer#newPipe` that returns
     * an additive pipe
     * @return A newly created pipe by the `Consumer` class.
     */
    private static Pipe<Box<Integer>> newAddPipe(int value) {
        return Consumer.newPipe(v -> {
            v.setValue(v.getValue() + value);
            return v;
        }, 0, 150);
    }

    /**
     * A simple wrapper for `Consumer#newPipe` that returns
     * a multiplicative pipe
     * @return A newly created pipe by the `Consumer` class.
     */
    private static Pipe<Box<Integer>> newMulPipe(int value) {
        return Consumer.newPipe(v -> {
            v.setValue(v.getValue() * value);
            return v;
        }, 0, 150);
    }
}
