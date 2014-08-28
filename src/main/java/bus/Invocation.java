package bus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class Invocation {
    private final Method method;
    private final Object invokedObject;
    private final ThreadType threadType;
    private final boolean rePost;

    public Invocation(Object invokedObject, Method method) {
        this.invokedObject = invokedObject;
        this.method = method;
        Consume annotation = method.getAnnotation(Consume.class);
        this.threadType = annotation.threadType();
        this.rePost = annotation.rePost();
    }

    public Method getMethod() {
        return method;
    }

    public Object getInvokedObject() {
        return invokedObject;
    }

    public Object invoke(Object... arguments) {
        Object invoke = threadType.invoke(() -> invokeMethod(arguments));
        if (rePost) {
            return invoke;
        } else {
            return null;
        }
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
