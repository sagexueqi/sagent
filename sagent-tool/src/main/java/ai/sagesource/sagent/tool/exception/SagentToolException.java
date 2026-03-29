package ai.sagesource.sagent.tool.exception;

import ai.sagesource.sagent.base.exception.SagentException;

/**
 * Sagent 工具体系根异常
 *
 * @author: sage.xue
 * @time: 2026/3/29
 */
public class SagentToolException extends SagentException {

    public SagentToolException(String message) {
        super(message);
    }

    public SagentToolException(String message, Throwable cause) {
        super(message, cause);
    }

    public SagentToolException(Throwable cause) {
        super(cause);
    }
}
