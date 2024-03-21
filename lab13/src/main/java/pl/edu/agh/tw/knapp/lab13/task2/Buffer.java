package pl.edu.agh.tw.knapp.lab13.task2;

import org.jcsp.lang.CSProcess;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Buffer implements CSProcess {
    private final Supplier<Portion> supplier;
    private final Consumer<Portion> consumer;

    public Buffer(Supplier<Portion> supplier, Consumer<Portion> consumer) {
        this.supplier = supplier;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        while (true) {
            consumer.accept(supplier.get());
        }
    }
}
