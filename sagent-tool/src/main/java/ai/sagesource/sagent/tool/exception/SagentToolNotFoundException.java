package ai.sagesource.sagent.tool.exception;

import lombok.Getter;

/**
 * 执行阶段找不到工具异常
 *
 * @author: sage.xue
 * @time: 2026/3/29
 */
@Getter
public class SagentToolNotFoundException extends SagentToolException {

    private final String toolName;

    public SagentToolNotFoundException(String toolName) {
        super("Tool not found: " + toolName);
        this.toolName = toolName;
    }
}
