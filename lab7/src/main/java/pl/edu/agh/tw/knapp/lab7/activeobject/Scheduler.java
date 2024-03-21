package pl.edu.agh.tw.knapp.lab7.activeobject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class Scheduler extends Thread {
    private final MethodRequestQueue<MethodRequest<?>> methodRequestQueue = new MethodRequestQueue<>();
    private final Map<MethodRequest<?>, SimpleFuture<?>> methodRequestFutureMap = new HashMap<>();

    private boolean isRunning = true;

    private int autoDispatchMod = 5;

    public void setAutoDispatchMod(int autoDispatchMod) {
        this.autoDispatchMod = autoDispatchMod;
    }

    public int getAutoDispatchMod() {
        return autoDispatchMod;
    }

    public <T> Future<T> enqueue(MethodRequest<T> methodRequest) {
        var future = new SimpleFuture<T>();

        synchronized (this) {
            methodRequestFutureMap.put(methodRequest, future);
            methodRequestQueue.put(methodRequest);

            if (methodRequestQueue.size() % autoDispatchMod == 0) {
                dispatch();
            }
        }

        return future;
    }

    public synchronized void dispatch() {
        notify();
    }

    public synchronized void shutdown() {
        isRunning = false;
        notify();
    }

    @Override
    public synchronized void run() {
        while (isRunning) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            methodRequestQueue.forEachReady(methodRequest ->
                    methodRequestFutureMap.remove(methodRequest).put(methodRequest.call()));
        }
    }
}
