package bus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.Callable;

import static bus.BusUtil.hashOf;

/**
 * This class holds the basic information about an consumer endpoint, that is the consumer
 * object and the consumer method, and supports invoking this method having provided arguments.
 */

final class Invocation {
    private final Method method;
    private final Object invokedObject;
    private final Consume annotation;

    /**
     * @param invokedObject the object which is invoked when argument is posted
     * @param method        the method which should be invoked and annotated with @Consume
     */
    Invocation(Object invokedObject, Method method) {
        this.invokedObject = invokedObject;
        this.method = method;
        this.annotation = method.getAnnotation(Consume.class);
    }

    Method getMethod() {
        return method;
    }

    Object getInvokedObject() {
        return invokedObject;
    }

    /**
     * Invokes the given arguments on the method and objects.
     *
     * @param arguments the posted arguments
     * @return the result of the method called
     */
    Optional<Object> invoke(Object... arguments) {
        ThreadType threadType = annotation.threadType();
        Callable<Object> callable = () -> invokeMethod(arguments);
        Optional<Object> returnedObject = threadType.invoke(callable, annotation);
        boolean rePost = annotation.rePost();
        if (shouldRepost(rePost, returnedObject, arguments)) {
            return Optional.of(returnedObject);
        } else {
            return Optional.empty();
        }
    }

    private boolean shouldRepost(boolean rePost, Optional<Object> returned, Object[] arguments) {
        return rePost && returned.isPresent() &&
                !hashOf(returned.get()).equals(hashOf(arguments));
    }


    private Object invokeMethod(Object... arguments) {
        try {
            return method.invoke(invokedObject, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ConfigurationException("Cannot invoke method '" + method.getName() +
                    "' from class '" + invokedObject.getClass().getName() + "' on object '"
                    + invokedObject + "' ", e);
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
