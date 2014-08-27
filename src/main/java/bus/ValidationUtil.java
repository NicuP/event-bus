package bus;

import java.lang.reflect.Method;

class ValidationUtil {

    static void validateConsumerIsAnnotated(Object consumer) {
        Class<?> cls = consumer.getClass();
        for (Method method : cls.getMethods()) {
            if (method.isAnnotationPresent(Consume.class)) {
                return;
            }
        }
        throw new IllegalArgumentException("Given object of class '" + cls +
                "' does not have any method annotated with @Consume");
    }


    static void notNulArguments(Object... events) {
        for (int i = 0; i < events.length; i++) {
            if (events[i] == null) {
                throw new NullPointerException("Argument number " + (i + 1) +
                        "of type " + events[i].getClass() + " is null; " +
                        "posting null objects is not allowed");
            }
        }
    }

    static void notNull(Object argument, String argumentName) {
        if (argument == null) {
            throw new NullPointerException(argumentName + " must not be null");
        }
    }
}
