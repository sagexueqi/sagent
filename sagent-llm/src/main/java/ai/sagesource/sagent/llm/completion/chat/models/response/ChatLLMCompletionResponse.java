package ai.sagesource.sagent.llm.completion.chat.models.response;

import ai.sagesource.sagent.llm.completion.LLMCompletionResponse;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

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
    private String                                  content;
    /**
     * Message Role
     */
    private String                                  role;
    /**
     * Tool Call List
     */
    private List<ChatLLMCompletionResponseToolCall> toolCalls;
}
