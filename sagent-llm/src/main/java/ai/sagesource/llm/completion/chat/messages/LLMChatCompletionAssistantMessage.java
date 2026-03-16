package ai.sagesource.llm.completion.chat.messages;

import java.util.Map;

/**
 * Assistant Role Chat Completion Message
 *
 * @author: sage.xue
 * @time: 2026/3/16
 */
public class LLMChatCompletionAssistantMessage extends ChatCompletionMessage {

    public LLMChatCompletionAssistantMessage(String content, Map<String, Object> additional) {
        super(content, additional);
    }

    public LLMChatCompletionAssistantMessage(String content) {
        super(content);
    }

    @Override
    public String role() {
        return "assistant";
    }
}
