package bus;

public class TypeSafeEventBus implements EventBus<Consumer> {
    @Override
    public void registerConsumer(String group, Consumer consumer) {

    }

    @Override
    public void registerConsumer(Consumer consumer) {

    }

    @Override
    public void postEvent(String group, Object... events) {

    }

    @Override
    public void post(Object... events) {

    }
}
