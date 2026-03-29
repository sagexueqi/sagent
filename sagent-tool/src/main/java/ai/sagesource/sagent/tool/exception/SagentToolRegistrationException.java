package ai.sagesource.sagent.tool.exception;

import lombok.Getter;

/**
 * 工具注册阶段异常。
 * 常见场景：
 * - 工具类缺少 @Tool 注解
 * - 找不到 execute 方法，也没有 @ToolMethod 标记
 * - execute 方法的参数缺少 @ToolParam 注解
 * - 同名工具重复注册
 *
 * @author: sage.xue
 * @time: 2026/3/29
 */
@Getter
public class SagentToolRegistrationException extends SagentToolException {

    private final String toolClass;

    public SagentToolRegistrationException(String message, String toolClass) {
        super(message);
        this.toolClass = toolClass;
    }

    public SagentToolRegistrationException(String message, String toolClass, Throwable cause) {
        super(message, cause);
        this.toolClass = toolClass;
    }
}
