package bus;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class BusUtil {
    private BusUtil() {}

    static String hashOf(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return innerHash(Arrays.stream(parameterTypes));
    }

    static String hashOf(Object... parameters) {
        Stream<Class<?>> classes = Arrays.stream(parameters)
                .map(Object::getClass);
        return innerHash(classes);
    }

    private static String innerHash(Stream<Class<?>> classes) {
        return classes.map(Class::getName)
                .collect(Collectors.joining("_"));
    }

    static String getTypes(Object[] arguments) {
        return Arrays.stream(arguments)
                .map(Object::getClass)
                .collect(Collectors.toList())
                .toString();
    }

    static String getGroupName(String group) {
        if ("".equals(group)) {
            return "(DEFAULT_GROUP)";
        } else {
            return group;
        }

    }


}
