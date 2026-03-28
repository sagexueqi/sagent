package ai.sagesource.sagent.llm.completion.chat;

import ai.sagesource.sagent.llm.exceptions.SagentLLMException;
import ai.sagesource.sagent.llm.client.LLMClient;
import ai.sagesource.sagent.llm.completion.LLMCompletion;
import ai.sagesource.sagent.llm.completion.LLMCompletionStreamingCallback;
import ai.sagesource.sagent.llm.completion.LLMCompletionStreamingHandle;
import ai.sagesource.sagent.llm.completion.chat.models.response.ChatLLMCompletionResponse;

/**
 * Chat Completion Abstract Class
 * <p>
 * CLIENT: Actual LLMClient Implement
 *
 * @author: sage.xue
 * @time: 2026/3/16
 */
public abstract class ChatLLMCompletion<LLM_CLIENT extends LLMClient<?>> implements LLMCompletion<ChatLLMCompletionResponse> {

    protected LLM_CLIENT llmClient;

    public ChatLLMCompletion(LLM_CLIENT llmClient) {
        this.llmClient = llmClient;
    }

    /**
     * 各实现层覆写此方法，把 SDK 特定异常翻译成框架异常
     */
    protected abstract SagentLLMException translateException(Exception e);

    /**
     * Streaming异常处理
     *
     * @param e
     * @param handle
     * @param callback
     */
    protected void handleStreamingException(Exception e,
                                            LLMCompletionStreamingHandle handle,
                                            LLMCompletionStreamingCallback<ChatLLMCompletionResponse> callback) {
        if (handle.isCancelled()) {
            // 取消引发的异常，走 onCancelled 而不是 onError
            callback.onCancelled();
        } else {
            SagentLLMException translated = translateException(e);
            callback.onError(translated);
        }
    }
}
