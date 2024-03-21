package pl.edu.agh.tw.knapp.lab13.task2;

public class Timer {
    private long startTime = 0;
    private long endTime = 0;

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void end() {
        endTime = System.currentTimeMillis();
    }

    public long elapsedTime() {
        return endTime - startTime;
    }

    @Override
    public String toString() {
        return endTime > 0 ?
                String.format("Timer(elapsed=%sms)", elapsedTime()) :
                String.format("Timer(startTime=%sms)", startTime);
    }
}
