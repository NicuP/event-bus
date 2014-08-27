package bus;

public interface Subscriber<T> {
    void invoke(T... events);
}
