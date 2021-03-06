package bus;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

class ValidationUtil {
    private ValidationUtil() {
    }

    static void validateConsumerIsAnnotated(Object consumer) {
        Class<?> cls = consumer.getClass();
        Method[] methods = cls.getMethods();
        Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(Consume.class))
                .findAny()
                .orElseThrow(() -> new ConfigurationException("Given object of class '"
                        + cls + "' as a consumer does not have any method annotated " +
                        "with @Consume"));
    }


    static void notNullArguments(Object... events) {
        for (int i = 0; i < events.length; i++) {
            if (events[i] == null) {
                throw new NullPointerException("Argument number " + (i + 1) +
                        "of type " + events[i].getClass() + " is null; " +
                        "posting null objects is not allowed");
            }
        }
    }

    static void notNull(Object argument, String argumentName) {
        Objects.requireNonNull(argument, "Argument '" + argumentName + "' must not be null");
    }
}
