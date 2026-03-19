package ai.sagesource.sagent.base.exception;

/**
 * Sagent LLM Exception
 *
 * @author: sage.xue
 * @time: 2026/3/19
 */
public class SagentLLMException extends SagentException {
    public SagentLLMException(String message) {
        super(message);
    }

    public SagentLLMException(String message, Throwable cause) {
        super(message, cause);
    }

    public SagentLLMException(Throwable cause) {
        super(cause);
    }
}
