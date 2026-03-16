package ai.sagesource.llm.completion.chat.messages;

import java.util.Map;

/**
 * System Role Chat Completion Message
 *
 * @author: sage.xue
 * @time: 2026/3/16
 */
public class LLMChatCompletionSystemMessage extends ChatCompletionMessage {

    public LLMChatCompletionSystemMessage(String content, Map<String, Object> additional) {
        super(content, additional);
    }

    public LLMChatCompletionSystemMessage(String content) {
        super(content);
    }

    @Override
    public String role() {
        return "system";
    }
}
