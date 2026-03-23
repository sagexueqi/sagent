package ai.sagesource.sagent.llm.completion;

/**
 * Streaming Callback
 *
 * @author: sage.xue
 * @time: 2026/3/23
 */
public interface LLMCompletionStreamingCallback<T extends LLMCompletionResponse> {

    /**
     * pre token output
     *
     * @param llmCompletionResponse
     */
    void onToken(T llmCompletionResponse);

    /**
     * all token output finish
     *
     * @param llmCompletionResponse
     */
    void onCompletion(T llmCompletionResponse);

    /**
     * exception callback
     *
     * @param e
     */
    void onError(Exception e);
}
