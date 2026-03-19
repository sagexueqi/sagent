package ai.sagesource.sagent.llm.completion.chat.models.messages;

import java.util.Map;

/**
 * System Role Chat Completion Message
 *
 * @author: sage.xue
 * @time: 2026/3/16
 */
public class ChatLLMCompletionSystemMessage extends ChatLLMCompletionMessage {

    public ChatLLMCompletionSystemMessage(String content, Map<String, Object> additional) {
        super(content, additional);
    }

    public ChatLLMCompletionSystemMessage(String content) {
        super(content);
    }

    @Override
    public String role() {
        return "system";
    }
}
