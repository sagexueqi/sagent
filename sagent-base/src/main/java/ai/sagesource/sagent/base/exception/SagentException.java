package ai.sagesource.sagent.base.exception;

/**
 * SagentException Base
 *
 * @author: sage.xue
 * @time: 2026/3/19
 */
public class SagentException extends RuntimeException {

    public SagentException(String message) {
        super(message);
    }

    public SagentException(String message, Throwable cause) {
        super(message, cause);
    }

    public SagentException(Throwable cause) {
        super(cause);
    }

    public SagentException() {
    }
}
