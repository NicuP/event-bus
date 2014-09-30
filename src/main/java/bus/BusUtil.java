package bus;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utilities class
 */
class BusUtil {
    private BusUtil() {
    }

    /**
     * This method returns the hash from a list of objects, based on the arguments of the
     * given method.
     * Only two methods that have the exact arguments and order of arguments should have
     * equal hash.
     * @param method the method whose parameters are considered for hashing
     * @return the hash based on the parameters types and position
     */
    static String hashOf(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return innerHash(Arrays.stream(parameterTypes));
    }

    /**
     * This method returns the hash from a list of objects;
     * Only two methods that have the exact arguments and order of arguments should have
     * equal hash.
     * @param parameters instances whose classes are used for obtaining the hash
     * @return the hash based on the parameters types and position
     */
    static String hashOf(Object... parameters) {
        Stream<Class<?>> classes = Arrays.stream(parameters)
                .map(Object::getClass);
        return innerHash(classes);
    }

    private static String innerHash(Stream<Class<?>> classes) {
        return classes.map(Class::getName)
                .collect(Collectors.joining("_"));
    }

    /**
     * @param arguments a list of objects
     * @return a list containing the class types of the given objects
     */
    static List<Class<?>> getTypes(Object[] arguments) {
        return Arrays.stream(arguments)
                .map(Object::getClass)
                .collect(Collectors.toList());
    }

    static String getGroupName(String group) {
        if (EventBus.DEFAULT_GROUP.equals(group)) {
            return "(DEFAULT_GROUP)";
        } else {
            return group;
        }

    }
}
