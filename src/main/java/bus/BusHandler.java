package bus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static bus.BusUtil.*;

class BusHandler {
    private Map<String, Holder> holders;

    BusHandler() {
        this.holders = new ConcurrentHashMap<>();
    }

    void register(String group, Object consumer) {
        Holder holder = holders.computeIfAbsent(group, (s) -> new Holder());
        List<Method> methods = getMethods(consumer);
        for (Method method : methods) {
            holder.registerMethod(consumer, method);
        }
    }

    private List<Method> getMethods(Object object) {
        List<Method> methods = new ArrayList<>();
        for (Method method : object.getClass().getMethods()) {
            if (method.isAnnotationPresent(Consume.class)) {
                methods.add(method);
            }
        }
        return methods;
    }

    void postEvent(String group, Object... arguments) {
        Holder holder = holders.get(group);
        try {
            holder.postEvent(false, arguments);
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
