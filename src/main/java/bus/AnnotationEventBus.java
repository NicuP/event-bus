package bus;

import static bus.ValidationUtil.notNulArguments;
import static bus.ValidationUtil.notNull;
import static bus.ValidationUtil.validateConsumerIsAnnotated;

public class AnnotationEventBus implements EventBus<Object, Object> {

    @Override
    public void registerConsumer(String group, Object consumer) {
        notNull(group, "Group");
        validateConsumerIsAnnotated(consumer);

    }

    @Override
    public void postEvent(String group, Object... events) {
        notNull(group, "Group");
        notNulArguments(events);

    }
}
