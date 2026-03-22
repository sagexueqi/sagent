package ai.sagesource.sagent.llm.completion.chat.models.messages;

import java.util.Map;

/**
 * System Role Chat Completion Message
 *
 * @author: sage.xue
 * @time: 2026/3/16
 */
public class ChatLLMCompletionSystemMessage extends ChatLLMCompletionMessage {

    @Override
    public String role() {
        return "system";
    }
}
