package ai.sagesource.sagent.llm.exceptions;

import ai.sagesource.sagent.base.exception.SagentLLMException;

/**
 * 网络/连接层异常
 *
 * @author: sage.xue
 * @time: 2026/3/28
 */
public class SagentLLMConnectionException extends SagentLLMException {
    public SagentLLMConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
