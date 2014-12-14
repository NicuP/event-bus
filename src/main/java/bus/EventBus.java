package bus;

public interface EventBus<C, T> {
    public static final String DEFAULT_GROUP = "";

    /**
     * Register a consumer in the default group.
     *
     * @param consumer the consumer on which the events should be called
     */
    void registerConsumer(String group, C consumer);

    /**
     * Register a consumer in DEFAULT_GROUP.
     *
     * @param consumer the consumer on which the events should be called
     */
    default void registerConsumer(C consumer) {
        registerConsumer(DEFAULT_GROUP, consumer);
    }

    /**
     * Post an event in the specified group.
     *
     * @param group the group in which the event will be posted
     * @param events the events to be posted
     *
     * @throws bus.ConfigurationException thrown in case {@code EventBus} was not
     * configured correctly; for example posting in an unregistered group or posting
     * events that do not have matching
     * @throws bus.CodeException thrown in case client code throws exception; wraps
     * the client exception
     */
    void postEventInGroup(String group, T... events);

    /**
     * Post an event in the DEFAULT_GROUP.
     *
     * @param events the events to be posted
     *
     * @throws bus.ConfigurationException thrown in case {@code EventBus} was not
     * configured correctly; for example posting in an unregistered group or posting
     * events that do not have matching
     * @throws bus.CodeException thrown in case client code throws exception; wraps
     * the client exception
     */
    default void postEvent(T... events) {
        postEventInGroup(DEFAULT_GROUP, events);
    }
}
