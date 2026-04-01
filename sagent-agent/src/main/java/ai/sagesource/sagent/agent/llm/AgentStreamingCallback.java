package ai.sagesource.sagent.agent.llm;

/**
 * Agent层流式回调接口
 * 对应LLM层的LLMCompletionStreamingCallback，使用AgentLLMResponse进行数据传递
 *
 * @author: sage.xue
 * @time: 2026/4/1
 */
public interface AgentStreamingCallback {

    /**
     * 每个token到达时回调
     *
     * @param response Agent层响应片段
     * @return false 表示请求中断流
     */
    boolean onToken(AgentLLMResponse response);

    /**
     * 所有token输出完成时回调
     *
     * @param response 完整的Agent层响应
     */
    void onCompletion(AgentLLMResponse response);

    /**
     * 流被取消时回调
     */
    default void onCancelled() {
    }

    /**
     * 异常时回调
     */
    default void onError(Throwable t) {
    }
}
