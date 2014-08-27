package bus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class Invoker {
    private final static ExecutorService service = Executors.newCachedThreadPool();

    static Object invoke(ThreadType threadType, Method method,
                         Object instance, Object... arguments) {
        if (ThreadType.SINGLE_THREAD.equals(threadType)) {
            invoke(method, instance, arguments);
        } else if (ThreadType.NEW_THREAD.equals(threadType)) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            return handleMulti(method, instance, executorService, arguments);
        } else if (ThreadType.POOLED_THREAD.equals(threadType)) {
            return handleMulti(method, instance, service, arguments);
        }
        throw new IllegalStateException("");
    }

    private static Object handleMulti(Method method, Object instance, ExecutorService executorService, Object[] arguments) {
        Future<Object> future = executorService.submit(
                () -> invoke(method, instance, arguments));
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Object invoke(Method method, Object instance, Object... arguments) {
        try {
            return method.invoke(instance, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }
}
