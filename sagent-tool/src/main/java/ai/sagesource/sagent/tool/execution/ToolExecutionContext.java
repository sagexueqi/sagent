package ai.sagesource.sagent.tool.execution;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * 工具执行上下文
 *
 * @author: sage.xue
 * @time: 2026/3/19
 */
@Getter
@Builder
public class ToolExecutionContext {

    /**
     * 工具名称
     */
    private final String toolName;

    /**
     * 工具调用参数
     */
    private final Map<String, Object> parameters;
}
