package ai.sagesource.sagent.agent.llm;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Agent层LLM请求参数封装
 * 用于标识一次对话请求，支持对话上下文管理
 *
 * @author: sage.xue
 * @time: 2026/4/1
 */
@Data
@Builder
@Accessors(fluent = true)
public class AgentLLMRequest {

    /**
     * 对话上下文标识
     * 同一个对话窗口的所有请求应使用同一个contextId，为后续记忆管理与压缩做准备
     */
    private String contextId;

    /**
     * 用户输入内容
     */
    private String userInput;

    /**
     * 温度参数，控制生成随机性
     */
    private Float temperature;
}
