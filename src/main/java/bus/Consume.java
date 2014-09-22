package bus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Consume {
    public static final long DEFAULT_TIMEOUT = -1l;

    ThreadType threadType() default ThreadType.SINGLE_THREAD;
    boolean rePost() default false;
    long timeout() default DEFAULT_TIMEOUT;
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
