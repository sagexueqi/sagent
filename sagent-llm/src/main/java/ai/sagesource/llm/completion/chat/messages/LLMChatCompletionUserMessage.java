package ai.sagesource.llm.completion.chat.messages;

import java.util.Map;

/**
 * User Role Chat Completion Message
 *
 * @author: sage.xue
 * @time: 2026/3/16
 */
public class LLMChatCompletionUserMessage extends ChatCompletionMessage {

    public LLMChatCompletionUserMessage(String content, Map<String, Object> additional) {
        super(content, additional);
    }

    public LLMChatCompletionUserMessage(String content) {
        super(content);
    }

    @Override
    public String role() {
        return "user";
    }
}
