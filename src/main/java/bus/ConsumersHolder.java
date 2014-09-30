package bus;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static bus.BusUtil.hashOf;

/**
 * This class holds all the information about all the consumers, indexing them based
 * on the consuming method's parameters type and position.
 */
final class ConsumersHolder {
    /*Index by method parameters type*/
    private Map<String, List<Invocation>> holder;

    ConsumersHolder() {
        this.holder = new ConcurrentHashMap<>();
    }

    /**
     * Register a method for being consumed.
     * @param consumer the consumer object given by client
     * @param method method annotated with @Consume
     */
    void registerMethod(Object consumer, Method method) {
        String hash = hashOf(method);
        Invocation invocation = new Invocation(consumer, method);
        holder.computeIfAbsent(hash, (f) -> new CopyOnWriteArrayList<>())
                .add(invocation);
    }

    /**
     * @param isReposted true if this event is given directly from client or is the result
     *                   of another computation
     * @param arguments the arguments which are to be dispatched to consumer(s)
     */
    void postEvent(boolean isReposted, Object... arguments) {
        String hash = hashOf(arguments);
        List<Invocation> invocations = holder.get(hash);
        validate(isReposted, invocations, arguments);
        for (Invocation invocation : invocations) {
            Optional<Object> returned = invoke(invocation, arguments);
            if (returned.isPresent()) {
                postEvent(true, returned.get());
            }
        }
    }

    private Optional<Object> invoke(Invocation invocation, Object[] arguments) {
        try {
            return invocation.invoke(arguments);
        } catch (CodeException e) {
            throw new CodeException("An exception was thrown by method '" +
                    invocation.getMethod() + "' on object of class '" +
                    invocation.getInvokedObject().getClass() , e.getCause());
        } catch (InternalException e) {
            throw new InternalException("An unexpected exception occurred while " +
                    "invoking method '" + invocation.getMethod() + "' on object of " +
                    "class '" + invocation.getInvokedObject().getClass() , e.getCause());
        }
    }

    private void validate(boolean isReposted,
                          List<Invocation> invocations,
                          Object[] arguments) {
        if (invocations == null || invocations.isEmpty()) {
            String message = "Cannot match given argument(s) '" +
                    Arrays.toString(arguments) + "' of type(s) '" +
                    BusUtil.getTypes(arguments) + "' with any of the " +
                    "registered objects' methods annotated with " + Consume.class;
            if (isReposted) {
                message += "; consider that this object is reposted, which " +
                        "means that is returned by one of your methods " +
                        "annotated @Consume and not posted directly";
            }
            throw new ConfigurationException(message);
        }
    }
}
