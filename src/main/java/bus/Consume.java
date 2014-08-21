package bus;

public @interface Consume {
    ThreadType threadType() default ThreadType.SINGLE_THREAD;
}
