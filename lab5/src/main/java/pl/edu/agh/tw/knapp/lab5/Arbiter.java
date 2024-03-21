package pl.edu.agh.tw.knapp.lab5;

public abstract class Arbiter {
    private Table table;

    public abstract void acquireForks(Philosopher philosopher);

    public abstract void releaseForks(Philosopher philosopher);

    public void setTable(Table table) {
        this.table = table;
    }

    public Table getTable() {
        return table;
    }
}
