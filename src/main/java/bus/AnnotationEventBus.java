package bus;

import java.util.logging.Logger;

import static bus.BusValidator.*;

public class AnnotationEventBus implements EventBus<Object> {
    public static final String DEFAULT_GROUP = "";
    private static Logger logger = Logger.getLogger(AnnotationEventBus.class.getName());

    public void registerConsumer(String group, Object consumer) {

    }

    public void registerConsumer(Object consumer) {
        registerConsumer(DEFAULT_GROUP, consumer);
    }

    public void postEvent(String group, Object... events) {
        validatePostedArguments(group, events);

    }

    public void post(Object... events) {
        postEvent(DEFAULT_GROUP, events);
    }
}
