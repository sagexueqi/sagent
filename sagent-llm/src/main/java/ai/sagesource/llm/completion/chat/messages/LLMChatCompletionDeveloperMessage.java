package ai.sagesource.llm.completion.chat.messages;

import java.util.Map;

/**
 * Developer Role Chat Completion Message
 *
 * @author: sage.xue
 * @time: 2026/3/16
 */
public class LLMChatCompletionDeveloperMessage extends ChatCompletionMessage {

    public LLMChatCompletionDeveloperMessage(String content, Map<String, Object> additional) {
        super(content, additional);
    }

    public LLMChatCompletionDeveloperMessage(String content) {
        super(content);
    }

    @Override
    public String role() {
        return "developer";
    }
}
