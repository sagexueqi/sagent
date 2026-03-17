package ai.sagesource.llm.completion.chat.models.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * A call to a function tool created by the model
 *
 * @author: sage.xue
 * @time: 2026/3/17
 */
@Data
@Accessors(fluent = true)
public class ChatLLMCompletionResponseFunctionToolCall {

    private String              id;
    private String              functionName;
    private Map<String, String> arguments;
}
