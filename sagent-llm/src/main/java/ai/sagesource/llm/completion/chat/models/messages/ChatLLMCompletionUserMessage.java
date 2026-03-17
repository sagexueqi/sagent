package ai.sagesource.llm.completion.chat.models.messages;

import java.util.Map;

/**
 * User Role Chat Completion Message
 *
 * @author: sage.xue
 * @time: 2026/3/16
 */
public class ChatLLMCompletionUserMessage extends ChatLLMCompletionMessage {

    public ChatLLMCompletionUserMessage(String content, Map<String, Object> additional) {
        super(content, additional);
    }

    public ChatLLMCompletionUserMessage(String content) {
        super(content);
    }

    @Override
    public String role() {
        return "user";
    }
}
