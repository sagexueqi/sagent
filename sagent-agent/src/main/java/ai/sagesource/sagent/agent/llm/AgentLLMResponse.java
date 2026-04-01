package ai.sagesource.sagent.agent.llm;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Agent层LLM响应包装器
 * 极简设计，仅暴露调用方关心的文本内容
 *
 * @author: sage.xue
 * @time: 2026/4/1
 */
@Data
@Builder
@Accessors(fluent = true)
public class AgentLLMResponse {

    /**
     * 模型生成的文本内容
     */
    private String content;
}
