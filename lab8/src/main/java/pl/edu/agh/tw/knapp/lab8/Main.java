package pl.edu.agh.tw.knapp.lab8;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class Main {
    private final static int TO_CENTER = Integer.MIN_VALUE;

    private record Params(
            int width,
            int height,
            int translateX,
            int translateY,
            int n,
            int poolSize,
            int maxIter,
            double zoom,
            boolean visualize
    ) {}

    private record Pair<F, S>(F first, S second) {}

    private enum ExecutorType {
        SINGLE_THREAD,
        FIXED_THREAD_POOL,
        CACHED_THREAD_POOL,
        WORK_STEALING_POOL
    }

    /**
     * The application's entry point
     * @param args The arguments list:<br>
     *             args[0]: Width, `int`<br>
     *             args[1]: Height, `int`<br>
     *             args[2]: X translation, can be set to `TO_CENTER`, `int`<br>
     *             args[3]: Y translation, can be set to `TO_CENTER`, `int`<br>
     *             args[4]: The number of chunks, `int`<br>
     *             args[5]: The number of threads in thread pool, `int`<br>
     *                      Note: this value will be only used if the executor type
     *                      is set to `FIXED_THREAD_POOL`<br>
     *             args[6]: The maximum number of iterations, `int`<br>
     *             args[7]: Zoom, `double`<br>
     *             args[8]: Whether to visualize or just perform calculations, `boolean`<br>
     *             args[9]: The executor type, available values: `SINGLE_THREAD`, `FIXED_THREAD_POOL`,
     *                      `CACHED_THREAD_POOL`, `WORK_STEALING_POOL`.
     */
    public static void main(String[] args) {
        var params = new Params(800, 600, 400, 300, 16, 8, 570, 150, true);
        var executorService = Executors.newFixedThreadPool(params.poolSize);

        // parse args, if specified
        if (args.length > 0) {
            if (args.length != 10)
                throw new IllegalArgumentException("expected 10 arguments, got " + args.length);

            BiFunction<Integer, Integer, Integer> translateVal = (value, dim) ->
                    value.equals(TO_CENTER) ? dim / 2 : value;

            var width = Integer.parseInt(args[0]);
            var height = Integer.parseInt(args[1]);

            params = new Params(
                    width,
                    height,
                    translateVal.apply(Integer.parseInt(args[2]), width),
                    translateVal.apply(Integer.parseInt(args[3]), height),
                    Integer.parseInt(args[4]),
                    Integer.parseInt(args[5]),
                    Integer.parseInt(args[6]),
                    Double.parseDouble(args[7]),
                    Boolean.parseBoolean(args[8]));

            executorService = switch (ExecutorType.valueOf(args[9].toUpperCase())) {
                case SINGLE_THREAD -> Executors.newSingleThreadExecutor();
                case FIXED_THREAD_POOL -> Executors.newFixedThreadPool(params.poolSize);
                case CACHED_THREAD_POOL -> Executors.newCachedThreadPool();
                case WORK_STEALING_POOL -> Executors.newWorkStealingPool();
            };
        }

        System.out.printf("time=%s\n", demo(params, executorService));

        executorService.shutdown();
    }

    private static long demo(Params params, ExecutorService executorService) {
        int width = params.width;
        int height = params.height;
        int n = params.n;
        int step = width / n;

        var chunks = new ArrayList<Pair<MandelbrotChunk, Future<?>>>(n);

        BiConsumer<Integer, Integer> mkChunk  = (startX, endX) -> {
            var chunkParams = new MandelbrotChunk.Params(
                    startX, 0,
                    endX, height,
                    params.translateX, params.translateY,
                    params.maxIter, params.zoom);

            var chunk = new MandelbrotChunk(chunkParams);

            chunks.add(new Pair<>(chunk, executorService.submit(chunk::calculate)));
        };

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < n - 1; ++i) {
            int startX = step * i;
            int endX = startX + step;
            mkChunk.accept(startX, endX);
        }

        {
            int startX = step * (n - 1);
            int endX = params.width;
            mkChunk.accept(startX, endX);
        }

        // wait for chunks to calculate
        chunks.forEach(pair -> {
            try {
                pair.second.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        long totalTime = System.currentTimeMillis() - startTime;

        if (params.visualize) {
            MandelbrotVisualizer.visualize(chunks.stream().map(Pair::first).toList());
        }

        return totalTime;
    }
}