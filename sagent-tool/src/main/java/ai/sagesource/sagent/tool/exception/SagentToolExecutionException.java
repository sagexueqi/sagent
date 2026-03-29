package ai.sagesource.sagent.tool.exception;

import lombok.Getter;

/**
 * 工具执行阶段异常（包括参数类型转换失败、反射调用失败等）
 *
 * @author: sage.xue
 * @time: 2026/3/29
 */
@Getter
public class SagentToolExecutionException extends SagentToolException {

    private final String toolName;

    public SagentToolExecutionException(String toolName, String message) {
        super(message);
        this.toolName = toolName;
    }

    public SagentToolExecutionException(String toolName, String message, Throwable cause) {
        super(message, cause);
        this.toolName = toolName;
    }
}
