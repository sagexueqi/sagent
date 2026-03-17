package ai.sagesource.llm.completion.chat.models.messages;

import java.util.Map;

/**
 * Developer Role Chat Completion Message
 *
 * @author: sage.xue
 * @time: 2026/3/16
 */
public class ChatLLMCompletionDeveloperMessage extends ChatLLMCompletionMessage {

    public ChatLLMCompletionDeveloperMessage(String content, Map<String, Object> additional) {
        super(content, additional);
    }

    public ChatLLMCompletionDeveloperMessage(String content) {
        super(content);
    }

    @Override
    public String role() {
        return "developer";
    }
}
