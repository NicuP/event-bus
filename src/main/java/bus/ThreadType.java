package bus;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.BiFunction;

import static bus.ThreadType.ExecutorHolder.executor;

public enum ThreadType {
    SINGLE_THREAD((callable, consume) -> {
        try {
            Object result = callable.call();
            return Optional.of(result);
        } catch (Exception e) {
            throw new CodeException(e);
        }
    }),
    NEW_THREAD((callable, consume) -> {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<?> future = executorService.submit(callable);
        Optional<Object> response = getResponse(future, consume);
        executorService.shutdown();
        return response;
    }),
    POOLED_THREAD((callable, consume) -> {
        Future<?> future = executor.submit(callable);
        return getResponse(future, consume);
    });

    private BiFunction<Callable<?>, Consume, Optional<Object>> invoker;

    ThreadType(BiFunction<Callable<?>, Consume, Optional<Object>> invoker) {
        this.invoker = invoker;
    }

    Optional<Object> invoke(Callable<?> callable, Consume invocationInfo) {
        return invoker.apply(callable, invocationInfo);
    }

    private static Optional<Object> getResponse(Future<?> future, Consume invocationInfo) {
        try {
            if (!invocationInfo.waitForResponse()) {
                return Optional.empty();
            }
            long timeout = invocationInfo.timeout();
            if (timeout == Consume.DEFAULT_TIMEOUT) {
                Object result = future.get();
                return Optional.of(result);
            } else {
                TimeUnit timeUnit = invocationInfo.timeUnit();
                Object result = future.get(timeout, timeUnit);
                return Optional.of(result);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InternalException(e);
        } catch (ExecutionException | TimeoutException e) {
            throw new CodeException(e);
        }
    }

    static class ExecutorHolder {
        static ExecutorService executor;

        static {
            executor = Executors.newCachedThreadPool();
            Thread cleanupThread = new Thread(executor::shutdown);
            Runtime.getRuntime().addShutdownHook(cleanupThread);
        }
    }
}
