package bus.model;

import java.lang.reflect.Method;

public class ParamterConverter {
    String convert(Method method) {
        return convert(method.getParameterTypes());
    }

    String convert(Object... parameters) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            stringBuilder.append(parameters[i].getClass()).append(i);
        }
        return stringBuilder.toString();
    }
}
