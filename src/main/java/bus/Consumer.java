package bus;

public interface Consumer<T, R> {
    R invoke(T t);
}
