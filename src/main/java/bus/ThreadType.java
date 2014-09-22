package bus;

import java.util.concurrent.*;

import static bus.ThreadType.ExecutorHolder.executor;

public enum ThreadType {
    SINGLE_THREAD((callable, timeout, timeUnit) -> {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new CodeException(e);
        }
    }),
    NEW_THREAD((callable, timeout, timeUnit) -> {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<?> future = executorService.submit(callable);
        Object response = getResponse(future, timeout, timeUnit);
        executorService.shutdown();
        return response;
    }),
    POOLED_THREAD((callable, timeout, timeUnit) -> {
        Future<?> future = executor.submit(callable);
        return getResponse(future, timeout, timeUnit);
    });

    private TriFunction<Callable<?>, Long, TimeUnit, ?> invoker;

    ThreadType(TriFunction<Callable<?>, Long, TimeUnit, ?> invoker) {
        this.invoker = invoker;
    }

    public Object invoke(Callable<?> callable, Long timeout, TimeUnit timeUnit) {
        return invoker.apply(callable, timeout, timeUnit);
    }

    private static Object getResponse(Future<?> future, long timeout, TimeUnit timeUnit) {
        try {
            if (timeout == Consume.DEFAULT_TIMEOUT) {
                return future.get();
            } else {
                return future.get(timeout, timeUnit);
            }
        } catch (InterruptedException e) {
            throw new InternalException(e);
        } catch (ExecutionException | TimeoutException e) {
            throw new CodeException(e);
        }
    }

    static class ExecutorHolder {
        static ExecutorService executor = Executors.newCachedThreadPool();
    }

    @FunctionalInterface
    static interface TriFunction<P1, P2, P3, R> {
        R apply(P1 p1, P2 p2, P3 p3);
    }
}
