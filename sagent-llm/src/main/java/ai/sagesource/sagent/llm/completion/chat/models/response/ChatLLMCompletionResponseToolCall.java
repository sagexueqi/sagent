package ai.sagesource.sagent.llm.completion.chat.models.response;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Chat Completion Response Tool Call Model
 *
 * @author: sage.xue
 * @time: 2026/3/17
 */
@Data
@Accessors(fluent = true)
public class ChatLLMCompletionResponseToolCall {

    /**
     * call function
     */
    private ChatLLMCompletionResponseFunctionToolCall function;
}
