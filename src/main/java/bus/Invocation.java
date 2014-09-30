package bus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static bus.BusUtil.hashOf;

final class Invocation {
    private final Method method;
    private final Object invokedObject;
    private final Consume annotation;

    public Invocation(Object invokedObject, Method method) {
        this.invokedObject = invokedObject;
        this.method = method;
        this.annotation = method.getAnnotation(Consume.class);
    }

    public Method getMethod() {
        return method;
    }

    public Object getInvokedObject() {
        return invokedObject;
    }

    public Object invoke(Object... arguments) {
        ThreadType threadType = annotation.threadType();
        long timeout = annotation.timeout();
        TimeUnit timeUnit = annotation.timeUnit();
        boolean waitForResponse = annotation.waitForResponse();
        Object returnedObject = threadType.invoke(() -> invokeMethod(arguments), timeout, timeUnit);
        boolean rePost = annotation.rePost();
        if (!rePost || isSameMethod(returnedObject, arguments)) {
            return null;
        } else {
            return returnedObject;
        }
    }

    /*Avoid stack overflow*/
    private boolean isSameMethod(Object returnedObject, Object... arguments) {
        return hashOf(returnedObject).equals(hashOf(arguments));
    }

    private Object invokeMethod(Object... arguments) {
        try {
            return method.invoke(invokedObject, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new InternalException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Invocation that = (Invocation) o;

        return method.equals(that.method);

    }

    @Override
    public int hashCode() {
        return method.hashCode();
    }
}
