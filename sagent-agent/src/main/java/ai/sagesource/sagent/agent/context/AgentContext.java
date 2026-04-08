package ai.sagesource.sagent.agent.context;

import ai.sagesource.sagent.llm.completion.chat.models.messages.ChatLLMCompletionMessage;

import java.util.List;

/**
 * Agent运行上下文接口
 * 封装Agent运行时的上下文信息，包括历史消息等
 *
 * @author: sage.xue
 * @time: 2026/4/6
 */
public interface AgentContext {

    /**
     * 获取上下文标识
     *
     * @return 上下文ID
     */
    String contextId();

    /**
     * 获取历史消息列表
     * 包含该上下文中的所有对话历史
     *
     * @return 历史消息列表
     */
    List<ChatLLMCompletionMessage> historyMessages();

    /**
     * 添加消息到上下文
     *
     * @param message 消息对象
     */
    void addMessage(ChatLLMCompletionMessage message);

    /**
     * 清空历史消息
     */
    void clearHistory();
}
