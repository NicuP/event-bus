package bus;

import java.util.concurrent.*;
import java.util.function.BiFunction;

import static bus.ThreadType.ExecutorHolder.executor;

public enum ThreadType {
    SINGLE_THREAD((callable, consume) -> {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new CodeException(e);
        }
    }),
    NEW_THREAD((callable, consume) -> {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<?> future = executorService.submit(callable);
        Object response = getResponse(future, consume);
        executorService.shutdown();
        return response;
    }),
    POOLED_THREAD((callable, consume) -> {
        Future<?> future = executor.submit(callable);
        return getResponse(future, consume);
    });

    private BiFunction<Callable<?>, Consume, ?> invoker;

    ThreadType(BiFunction<Callable<?>, Consume, ?> invoker) {
        this.invoker = invoker;
    }

    public Object invoke(Callable<?> callable, Consume consume) {
        return invoker.apply(callable, consume);
    }

    private static Object getResponse(Future<?> future, Consume consume) {
        try {
            long timeout = consume.timeout();
            if (!consume.waitForResponse()) {
                return null;
            }
            if (timeout == Consume.DEFAULT_TIMEOUT) {
                return future.get();
            } else {
                TimeUnit timeUnit = consume.timeUnit();
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
    static interface CvadriFunction<P1, P2, P3, P4, R> {
        R apply(P1 p1, P2 p2, P3 p3, P4 p4);
    }
}
