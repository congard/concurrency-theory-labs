package pl.edu.agh.tw.knapp.lab5.naive;

import pl.edu.agh.tw.knapp.lab5.Philosopher;

public class ForkAcquireException extends RuntimeException {
    public ForkAcquireException(Philosopher philosopher) {
        super(String.format("Philosopher #%s cannot acquire the fork(s)",
                philosopher.getIndex()));
    }
}
