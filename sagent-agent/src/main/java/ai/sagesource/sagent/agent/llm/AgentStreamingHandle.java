package ai.sagesource.sagent.agent.llm;

import ai.sagesource.sagent.llm.completion.LLMCompletionStreamingHandle;

import java.util.concurrent.TimeUnit;

/**
 * Agent层流式控制句柄
 * 对LLM层的LLMCompletionStreamingHandle做轻量级代理封装
 *
 * @author: sage.xue
 * @time: 2026/4/1
 */
public class AgentStreamingHandle implements AutoCloseable {

    private final LLMCompletionStreamingHandle delegate;

    public AgentStreamingHandle(LLMCompletionStreamingHandle delegate) {
        this.delegate = delegate;
    }

    /**
     * 取消流式输出
     */
    public void cancel() {
        delegate.cancel();
    }

    /**
     * 判断流是否已被取消
     */
    public boolean isCancelled() {
        return delegate.isCancelled();
    }

    /**
     * 等待流完成
     */
    public boolean awaitCompletion(long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.awaitCompletion(timeout, unit);
    }

    @Override
    public void close() {
        delegate.close();
    }
}
