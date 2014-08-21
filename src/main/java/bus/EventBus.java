package bus;

public interface EventBus<T> {
    public void registerConsumer(String group, T consumer);

    public void registerConsumer(T consumer);

    public void postEvent(String group, Object... events);

    public void post(Object... events);
}
