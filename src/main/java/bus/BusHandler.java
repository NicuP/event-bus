package bus;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static bus.BusUtil.getGroupName;

class BusHandler {
    private Map<String, ConsumersHolder> holders;

    BusHandler() {
        this.holders = new ConcurrentHashMap<>();
    }

    void register(String group, Object consumer) {
        ConsumersHolder consumersHolder = holders.computeIfAbsent(group, (s) -> new ConsumersHolder());
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

    void postEvent(String group, Object... arguments) {
        ConsumersHolder consumersHolder = holders.get(group);
        try {
            consumersHolder.postEvent(false, arguments);
        } catch (ConfigurationException e) {
            throw new ConfigurationException("In group '" + getGroupName(group)
                    + "', " + e.getMessage());
        }
    }

    boolean isGroupDefined(String group) {
        return holders.containsKey(group);
    }

    Collection<String> getRegisteredGroups() {
        return holders.keySet();
    }
}
