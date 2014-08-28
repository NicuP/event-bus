package bus;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

class BusUtil {
    private BusUtil() {}

    static String hashOf(Method method) {
        return internalHash(method.getParameterTypes());
    }

    static String hashOf(Object... parameters) {
        Class[] classes = Arrays.stream(parameters)
                .map(Object::getClass)
                .toArray(Class[]::new);
        return internalHash(classes);
    }

    private static String internalHash(Class... parameters) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            stringBuilder.append(parameters[i]).append(i);
        }
        return stringBuilder.toString();
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
