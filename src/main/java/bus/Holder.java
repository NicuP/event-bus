package bus;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static bus.BusUtil.hashOf;

final class Holder {
    private Map<String, List<Invocation>> holder;

    Holder() {
        this.holder = new ConcurrentHashMap<>();
    }

    void registerMethod(Object consumer, Method method) {
        String hash = hashOf(method);
        Invocation invocation = new Invocation(consumer, method);
        holder.computeIfAbsent(hash, (f) -> new CopyOnWriteArrayList<>())
                .add(invocation);
    }

    public void unregisterMethod(Object consumer, Method method) {
        String hash = hashOf(method);
        Invocation invocation = new Invocation(consumer, method);
        holder.get(hash).remove(invocation);//todo not sure if ok, find beter solution
    }

    void postEvent(boolean isReposted, Object... arguments) {
        String hash = hashOf(arguments);
        List<Invocation> invocations = holder.get(hash);
        validate(isReposted, invocations, arguments);
        for (Invocation invocation : invocations) {
            Object returned = invoke(invocation, arguments);
            if (returned != null) {
                postEvent(true, arguments);
            }
        }
    }

    private Object invoke(Invocation invocation, Object[] arguments) {
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
            String message = "cannot match given arguments '" +
                    Arrays.toString(arguments) + "' of types '" +
                    BusUtil.getTypes(arguments) + "'with any of the " +
                    "registered objects' methods annotated with @Consume";
            if (isReposted) {
                message += "; consider that this object is reposted, which " +
                        "means that is returned by one of your methods " +
                        "annotated @Consume and not posted directly";
            }
            throw new ConfigurationException(message);
        }
    }
}
