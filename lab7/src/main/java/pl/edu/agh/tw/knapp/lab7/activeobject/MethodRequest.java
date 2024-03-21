package pl.edu.agh.tw.knapp.lab7.activeobject;

public interface MethodRequest<T> {
    T call();
    boolean guard();
}
