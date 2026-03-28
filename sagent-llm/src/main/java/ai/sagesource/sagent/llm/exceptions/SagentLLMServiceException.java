package ai.sagesource.sagent.llm.exceptions;

import ai.sagesource.sagent.base.exception.SagentLLMException;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * LLM服务端异常
 *
 * @author: sage.xue
 * @time: 2026/3/28
 */
@Getter
@Accessors(fluent = true)
public class SagentLLMServiceException extends SagentLLMException {
    private final int    statusCode;
    private final String errorCode;

    public SagentLLMServiceException(int statusCode, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }
}
