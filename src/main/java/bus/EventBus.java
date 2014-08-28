package bus;

public interface EventBus<C, T> {
    static final String DEFAULT_GROUP = "";

    void registerConsumer(String group, C consumer);

    void unregisterConsumer(String group, C consumer);

    default void registerConsumer(C consumer) {
        registerConsumer(DEFAULT_GROUP, consumer);
    }

    void postEvent(String group, T... events);

    default void postEvent(T... events) {
        postEvent(DEFAULT_GROUP, events);
    }
}
