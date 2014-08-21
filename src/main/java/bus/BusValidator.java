package bus;

import java.util.logging.Logger;

class BusValidator {
    private static Logger logger = Logger.getLogger(BusValidator.class.getName());

    static void validatePostedArguments(String group, Object... events) {
        if (group == null) {
            throw new IllegalArgumentException("Group must not be null");
        }
        if (events.length == 0) {
            throw new IllegalArgumentException("At least one element " +
                    "must be posted");
        }
        for (int i = 0; i < events.length; i++) {
            if (events[i] == null) {
                throw new IllegalArgumentException("Object number " + (i + 1) +
                        " is null; posting null objects is not allowed");
            }
        }
    }
}
