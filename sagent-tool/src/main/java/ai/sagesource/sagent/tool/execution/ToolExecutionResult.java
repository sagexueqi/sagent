package ai.sagesource.sagent.tool.execution;

import lombok.Builder;
import lombok.Getter;

/**
 * 工具执行结果
 *
 * @author: sage.xue
 * @time: 2026/3/29
 */
@Getter
@Builder
public class ToolExecutionResult {

    /**
     * 执行是否成功
     */
    private final boolean success;

    /**
     * 返回给 LLM 的文本内容。
     * 成功时为工具的执行结果描述；失败时为错误说明。
     */
    private final String content;

    /**
     * 工具执行的原始数据对象（可选，供上层业务使用，不传给 LLM）
     */
    private final Object rawData;

    // ——— 静态工厂方法 ———

    public static ToolExecutionResult success(String content) {
        return ToolExecutionResult.builder()
                .success(true)
                .content(content)
                .build();
    }

    public static ToolExecutionResult success(String content, Object rawData) {
        return ToolExecutionResult.builder()
                .success(true)
                .content(content)
                .rawData(rawData)
                .build();
    }

    public static ToolExecutionResult failure(String content) {
        return ToolExecutionResult.builder()
                .success(false)
                .content(content)
                .build();
    }
}
