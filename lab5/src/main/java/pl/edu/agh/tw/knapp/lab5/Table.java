package pl.edu.agh.tw.knapp.lab5;

import java.util.List;

public record Table(List<? extends Philosopher> philosophers, List<Fork> forks) {
    public Table(List<? extends Philosopher> philosophers, List<Fork> forks) {
        if (philosophers.size() != forks.size())
            throw new IllegalArgumentException("The philosopher count must be equal to the fork count");

        this.philosophers = philosophers;
        this.forks = forks;

        philosophers.forEach(philosopher -> philosopher.setTable(this));
        forks.forEach(fork -> fork.setTable(this));
    }

    private enum Side {
        LEFT, RIGHT
    }

    private Fork getFork(Side side, Philosopher philosopher) {
        if (this != philosopher.getTable())
            throw new IllegalArgumentException("The table does not contain such a philosopher");

        int index = philosopher.getIndex();
        int forkIndex = (index + side.ordinal()) % forks.size();

        return forks.get(forkIndex);
    }

    public Fork getLeftFork(Philosopher philosopher) {
        return getFork(Side.LEFT, philosopher);
    }

    public Fork getRightFork(Philosopher philosopher) {
        return getFork(Side.RIGHT, philosopher);
    }
}
