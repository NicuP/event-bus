package bus;

import static bus.ValidationUtil.*;

public class AnnotationEventBus implements EventBus<Object, Object> {
    private BusHandler busHandler = new BusHandler();

    @Override
    public void registerConsumer(String group, Object consumer) {
        notNull(group, "Group");
        validateConsumerIsAnnotated(consumer);
        busHandler.register(group, consumer);
    }

    @Override
    public void unregisterConsumer(String group, Object consumer) {
        notNull(group, "Group");
        validateConsumerIsAnnotated(consumer);
        executeIfGroupExists(group, () -> busHandler.unregister(group, consumer));
    }

    @Override
    public void postEvent(String group, Object... events) {
        notNull(group, "Group");
        notNulArguments(events);
        executeIfGroupExists(group, () -> busHandler.postEvent(group, events));
    }

    private void executeIfGroupExists(String group, Runnable runnable) {
        if (busHandler.isGroupDefined(group)) {
            runnable.run();
        } else {
            throw new ConfigurationException("Given group '" + group + "' is " +
                    "not registered; registered groups are, DEFAULT_GROUP " +
                    "(empty String) and " + busHandler.getRegisteredGroups());
        }
    }
}
