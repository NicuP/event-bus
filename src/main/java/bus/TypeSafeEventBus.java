package bus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static bus.ValidationUtil.notNullArguments;
import static bus.ValidationUtil.notNull;

public final class TypeSafeEventBus<T> implements EventBus<Subscriber<T>, T> {
    private final Map<String, List<Subscriber<T>>> consumers;

    public TypeSafeEventBus() {
        consumers = new ConcurrentHashMap<>();
        consumers.put(DEFAULT_GROUP, new CopyOnWriteArrayList<>());
    }

    @Override
    public void registerConsumer(String group, Subscriber<T> subscriber) {
        notNull(group, "Group");
        notNull(subscriber, "Subscriber");
        consumers.computeIfAbsent(group, s -> new CopyOnWriteArrayList<>())
                .add(subscriber);
    }

    @Override
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final void postEventInGroup(String group, T... events) {
        notNull(group, "Group");
        notNullArguments(events);
        List<Subscriber<T>> subscriberGroup = consumers.get(group);
        subscriberGroup.forEach((subscriber) -> subscriber.invoke(events));
    }
}
