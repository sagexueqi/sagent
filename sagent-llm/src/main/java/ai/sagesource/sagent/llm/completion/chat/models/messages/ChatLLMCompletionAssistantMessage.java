package ai.sagesource.sagent.llm.completion.chat.models.messages;

import ai.sagesource.sagent.llm.completion.chat.models.response.ChatLLMCompletionResponseToolCall;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Assistant Role Chat Completion Message
 *
 * @author: sage.xue
 * @time: 2026/3/16
 */
@Data
@Accessors(fluent = true)
public class ChatLLMCompletionAssistantMessage extends ChatLLMCompletionMessage {

    /**
     * Tool Call List
     */
    private List<ChatLLMCompletionResponseToolCall> toolCalls;

    @Override
    public String role() {
        return "assistant";
    }
}
