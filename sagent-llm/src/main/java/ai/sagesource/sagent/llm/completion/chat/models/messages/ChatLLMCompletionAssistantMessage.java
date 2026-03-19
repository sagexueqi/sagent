package ai.sagesource.sagent.llm.completion.chat.models.messages;

import java.util.Map;

/**
 * Assistant Role Chat Completion Message
 *
 * @author: sage.xue
 * @time: 2026/3/16
 */
public class ChatLLMCompletionAssistantMessage extends ChatLLMCompletionMessage {

    public ChatLLMCompletionAssistantMessage(String content, Map<String, Object> additional) {
        super(content, additional);
    }

    public ChatLLMCompletionAssistantMessage(String content) {
        super(content);
    }

    @Override
    public String role() {
        return "assistant";
    }
}
