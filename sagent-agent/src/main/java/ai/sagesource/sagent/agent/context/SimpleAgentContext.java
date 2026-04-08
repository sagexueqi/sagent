package ai.sagesource.sagent.agent.context;

import ai.sagesource.sagent.llm.completion.chat.models.messages.ChatLLMCompletionMessage;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 基础AgentContext实现
 * 简单的内存存储实现，仅维护历史消息列表
 *
 * @author: sage.xue
 * @time: 2026/4/6
 */
@Data
@Builder
@Accessors(fluent = true)
public class SimpleAgentContext implements AgentContext {

    /**
     * 上下文标识
     */
    private final String contextId;

    /**
     * 历史消息列表
     */
    @Builder.Default
    private final List<ChatLLMCompletionMessage> historyMessages = new ArrayList<>();

    /**
     * 创建新的空上下文，自动生成contextId
     */
    public static SimpleAgentContext create() {
        return SimpleAgentContext.builder()
                .contextId(UUID.randomUUID().toString())
                .build();
    }

    /**
     * 创建指定contextId的空上下文
     */
    public static SimpleAgentContext create(String contextId) {
        return SimpleAgentContext.builder()
                .contextId(contextId)
                .build();
    }

    @Override
    public void addMessage(ChatLLMCompletionMessage message) {
        if (message != null) {
            historyMessages.add(message);
        }
    }

    @Override
    public void clearHistory() {
        historyMessages.clear();
    }
}
