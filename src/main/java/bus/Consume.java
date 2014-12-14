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

    /**
     * Indicate whether or not the method should be called in the same thread.
     */
    ThreadType threadType() default ThreadType.SAME_THREAD;

    /**
     * Indicates whether the returned object of the annotated method should be automatically
     * reposted. The reposting is performed in the same group.
     * <br>
     * For example, the following code will pass the assert:
     * <br>
     * <pre class="code">
     * {@code
     *
     * &#064;Consume(rePost = true)
     * public String getStuff(MyObject myObject) {
     *    return "tada";
     * }
     *
     * &#064;Consume
     * public process(String s) {
     *     assert "tada".equals(s);
     * }
     * }
     *
     * bus.post(myObject);
     * </pre>
     *
     *
     * Note: Use with care not to create infinite recursion which lead to stack overflow; this
     * can happen if there is a method annotated with {@link &#064; bus.Consume} .
     * The library checks only for first-level recursion,  and will not repost in this case.
     *
     * Ignored if waitForResponse = true
     */
    boolean rePost() default false;

    /**
     * Used only if threadType is NEW_THREAD or POOLED_THREAD. Indicate how much time to wait
     * at most for method to execute; default is -1 which means wait indefinitely.
     */
    long timeout() default DEFAULT_TIMEOUT;

    /**
     * Used only if threadType is NEW_THREAD or POOLED_THREAD. Indicates the TimeUnit for timeout.
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * Used only if threadType is NEW_THREAD or POOLED_THREAD. Indicates whether the producer thread
     * should wait for consumer to finish.
     * If this is true, then rePost functionality is lost.
     */
    boolean waitForResponse() default false;
}
