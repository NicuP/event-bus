package bus.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Invocation {
    private static final Executor executor = Executors.newCachedThreadPool();
    private Method method;
    private Object object;

    public Invocation(Method method, Object object) {
        this.method = method;
        this.object = object;
    }

    public void invoke(Object... arguments) {
        try {
            method.invoke(object, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }
}
