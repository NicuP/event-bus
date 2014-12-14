package bus;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static bus.BusUtil.getGroupName;
import static bus.ValidationUtil.*;

public class AnnotationEventBus implements EventBus<Object, Object> {
    private Map<String, ConsumersHolder> holders;

    public AnnotationEventBus() {
        this.holders = new ConcurrentHashMap<>();
    }

    @Override
    public void registerConsumer(String group, Object consumer) {
        notNull(group, "Group");
        validateConsumerIsAnnotated(consumer);
        register(group, consumer);
    }

    private void register(String group, Object consumer) {
        ConsumersHolder consumersHolder =
                holders.computeIfAbsent(group, (s) -> new ConsumersHolder());
        List<Method> methods = getMethods(consumer);
        for (Method method : methods) {
            consumersHolder.registerMethod(consumer, method);
        }
    }

    private List<Method> getMethods(Object object) {
        Class<?> cls = object.getClass();
        Method[] methods = cls.getMethods();
        return Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(Consume.class))
                .collect(Collectors.toList());
    }

    @Override
    public void postEventInGroup(String group, Object... events) {
        notNull(group, "Group");
        notNullArguments(events);
        if (holders.containsKey(group)) {
            postEvent(group, events);
        } else {
            throw new ConfigurationException("Given group '" + group + "' is " +
                    "not registered; registered groups are, DEFAULT_GROUP " +
                    "(empty String) and " + holders.keySet());
        }
    }

    private void postEvent(String group, Object... arguments) {
        ConsumersHolder consumersHolder = holders.get(group);
        try {
            consumersHolder.postEvent(false, arguments);
        } catch (ConfigurationException e) {
            throw new ConfigurationException("In group '" + getGroupName(group)
                    + "', " + e.getMessage());
        }
    }
}
