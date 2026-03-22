package ai.sagesource.sagent.llm.completion.chat.models.messages;

import java.util.Map;

/**
 * User Role Chat Completion Message
 *
 * @author: sage.xue
 * @time: 2026/3/16
 */
public class ChatLLMCompletionToolMessage extends ChatLLMCompletionMessage {

    @Override
    public String role() {
        return "tool";
    }
}
