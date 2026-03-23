package ai.sagesource.sagent.llm.completion;

import ai.sagesource.sagent.llm.completion.chat.models.messages.ChatLLMCompletionMessage;
import ai.sagesource.sagent.llm.completion.chat.models.response.ChatLLMCompletionResponse;
import ai.sagesource.sagent.llm.function.FunctionToolDefinition;

import java.util.List;

/**
 * Completion Interface
 *
 * @author: sage.xue
 * @time: 2026/3/15
 */
public interface LLMCompletion {

    /**
     * Sync Thinking
     *
     * @param messages
     * @param functions
     * @param temperature
     * @return
     */
    ChatLLMCompletionResponse thinking(List<ChatLLMCompletionMessage> messages,
                                       List<FunctionToolDefinition> functions,
                                       float temperature);

    /**
     * Streaming Thinking
     *
     * @param messages
     * @param functions
     * @param temperature
     * @param streamingCallback
     * @return
     */
    ChatLLMCompletionResponse thinking_streaming(List<ChatLLMCompletionMessage> messages,
                                                 List<FunctionToolDefinition> functions,
                                                 float temperature,
                                                 LLMCompletionStreamingCallback<?> streamingCallback);
}
