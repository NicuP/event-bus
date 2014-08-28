package bus;

public class CodeException extends RuntimeException {

    public CodeException(Throwable cause) {
        super(cause);
    }

    public CodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
