package pl.edu.agh.tw.knapp.pipeline;

public abstract class Pipe<T> {
    private Pipe<T> next;
    private PipeAction<T> action;

    public Pipe<T> then(Pipe<T> next) {
        this.next = next;
        action = null;
        return next;
    }

    public void action(PipeAction<T> action) {
        this.action = action;
        next = null;
    }

    public abstract void close();

    public void closeAll() {
        close();

        if (next != null) {
            next.closeAll();
        }
    }

    public void submitAsync(T value) {
        throw new RuntimeException("Not implemented");
    }

    public void submit(T value) {
        submitValue(value, Pipe::submit);
    }

    protected void submitValue(T value, NextAction<T> nextAction) {
        var result = onSubmit(value);

        if (next != null) {
            nextAction.onNext(next, result);
        } else if (action != null) {
            action.onAction(result);
        }
    }

    protected abstract T onSubmit(T value);

    protected interface NextAction<T> {
        void onNext(Pipe<T> next, T value);
    }
}
