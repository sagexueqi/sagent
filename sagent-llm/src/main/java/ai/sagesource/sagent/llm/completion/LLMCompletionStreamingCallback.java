package ai.sagesource.sagent.llm.completion;

/**
 * Streaming Callback
 *
 * @author: sage.xue
 * @time: 2026/3/23
 */
public interface LLMCompletionStreamingCallback<T extends LLMCompletionResponse> {

    /**
     * 每个 token 到达时回调
     *
     * @param llmCompletionResponse
     * @return false 表示请求中断流
     */
    boolean onToken(T llmCompletionResponse);

    /**
     * 所有token输出完成
     *
     * @param llmCompletionResponse
     */
    void onCompletion(T llmCompletionResponse);

    /**
     * 流被取消时回调（区别于正常完成）
     */
    default void onCancelled() {
    }

    /**
     * 异常时回调
     */
    default void onError(Throwable t) {
    }
}
