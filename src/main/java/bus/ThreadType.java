package bus;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public enum ThreadType {
    SINGLE_THREAD(Runnable::run),
    NEW_THREAD(runnable -> new Thread(runnable).start()),
    POOLED_THREAD(ExecutorHolder.executor::execute);

    private Consumer<Runnable> invoker;

    ThreadType(Consumer<Runnable> invoker) {
        this.invoker = invoker;
    }

    public void invoke(Runnable runnable) {
        invoker.accept(runnable);
    }

    static class ExecutorHolder {
        static Executor executor = Executors.newCachedThreadPool();
    }
}
