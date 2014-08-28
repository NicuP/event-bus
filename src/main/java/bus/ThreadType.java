package bus;

import java.util.concurrent.*;
import java.util.function.Function;

import static bus.ThreadType.ExecutorHolder.*;

public enum ThreadType {
    SINGLE_THREAD((callable) -> {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new CodeException(e);
        }
    }),
    NEW_THREAD(callable -> {
        try {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<?> future = executorService.submit(callable);
            Object response = future.get();
            executorService.shutdown();
            return response;
        } catch (InterruptedException e) {
            throw new InternalException(e);
        } catch (ExecutionException e){
            throw new CodeException(e);
        }
    }),
    POOLED_THREAD(callable -> {
        try {
            Future<?> future = executor.submit(callable);
            return future.get();
        } catch (InterruptedException e) {
            throw new InternalException(e);
        } catch (ExecutionException e){
            throw new CodeException(e);
        }
    });

    private Function<Callable<?>, ?> invoker;

    ThreadType(Function<Callable<?>, ?> invoker) {
        this.invoker = invoker;
    }

    public Object invoke(Callable<?> callable) {
        return invoker.apply(callable);
    }

    static class ExecutorHolder {
        static ExecutorService executor = Executors.newCachedThreadPool();
    }
}
