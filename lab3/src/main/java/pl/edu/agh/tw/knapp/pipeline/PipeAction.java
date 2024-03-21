package pl.edu.agh.tw.knapp.pipeline;

public interface PipeAction<T> {
    void onAction(T value);
}
