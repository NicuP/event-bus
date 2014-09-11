package bus;

public interface EventBus<C, T> {
    static final String DEFAULT_GROUP = "";

    void registerConsumer(String group, C consumer);

    default void registerConsumer(C consumer) {
        registerConsumer(DEFAULT_GROUP, consumer);
    }

    void postEventInGroup(String group, T... events);

    default void postEvent(T... events) {
        postEventInGroup(DEFAULT_GROUP, events);
    }
}
