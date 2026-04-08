package ai.sagesource.sagent.agent.exception;

import ai.sagesource.sagent.base.exception.SagentException;

/**
 * Agent层运行时异常基类
 * 用于封装Agent执行过程中的业务异常
 *
 * @author: sage.xue
 * @time: 2026/4/6
 */
public class SagentAgentException extends SagentException {

    public SagentAgentException(String message) {
        super(message);
    }

    public SagentAgentException(String message, Throwable cause) {
        super(message, cause);
    }

    public SagentAgentException(Throwable cause) {
        super(cause);
    }

    /**
     * Agent未初始化异常
     */
    public static class NotInitializedException extends SagentAgentException {
        public NotInitializedException(String agentName) {
            super("Agent [" + agentName + "] is not initialized. Call initialize() first.");
        }
    }

    /**
     * Agent执行异常
     */
    public static class ExecutionException extends SagentAgentException {
        public ExecutionException(String agentName, String message, Throwable cause) {
            super("Agent [" + agentName + "] execution failed: " + message, cause);
        }
    }
}
