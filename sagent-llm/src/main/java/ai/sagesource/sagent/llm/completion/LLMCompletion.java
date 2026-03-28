package ai.sagesource.sagent.llm.completion;

import ai.sagesource.sagent.llm.completion.chat.models.messages.ChatLLMCompletionMessage;
import ai.sagesource.sagent.llm.function.FunctionToolDefinition;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Completion Interface
 *
 * @author: sage.xue
 * @time: 2026/3/15
 */
public interface LLMCompletion<R extends LLMCompletionResponse> {

    /**
     * 同步Thinking
     *
     * @param messages
     * @param functions
     * @param temperature
     * @return
     */
    R thinking(List<ChatLLMCompletionMessage> messages,
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
    LLMCompletionStreamingHandle thinking_streaming(List<ChatLLMCompletionMessage> messages,
                                                    List<FunctionToolDefinition> functions,
                                                    float temperature,
                                                    LLMCompletionStreamingCallback<R> streamingCallback);

    /**
     * 异步Streaming Thinking
     *
     * @param messages
     * @param functions
     * @param temperature
     * @param streamingCallback
     * @param executor
     * @return
     */
    LLMCompletionStreamingHandle thinking_stream_async(List<ChatLLMCompletionMessage> messages,
                                                       List<FunctionToolDefinition> functions,
                                                       float temperature,
                                                       LLMCompletionStreamingCallback<R> streamingCallback,
                                                       Executor executor);
}
