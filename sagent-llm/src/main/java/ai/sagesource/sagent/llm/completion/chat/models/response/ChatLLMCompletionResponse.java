package ai.sagesource.sagent.llm.completion.chat.models.response;

import ai.sagesource.sagent.llm.completion.LLMCompletionResponse;
import ai.sagesource.sagent.llm.completion.chat.models.messages.ChatLLMCompletionAssistantMessage;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Chat Completion Response Model
 *
 * @author: sage.xue
 * @time: 2026/3/17
 */
@Data
@Accessors(fluent = true)
public class ChatLLMCompletionResponse implements LLMCompletionResponse {

    /**
     * Response Chat Content
     */
    private ChatLLMCompletionAssistantMessage message;
    /**
     * tool calling flag
     */
    private boolean                           toolCalls;
}
