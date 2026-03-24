package ai.sagesource.sagent.llm.completion.chat;

import ai.sagesource.sagent.llm.client.LLMClient;
import ai.sagesource.sagent.llm.completion.LLMCompletion;
import ai.sagesource.sagent.llm.completion.LLMCompletionResponse;
import ai.sagesource.sagent.llm.completion.LLMCompletionStreamingCallback;
import ai.sagesource.sagent.llm.completion.chat.models.messages.ChatLLMCompletionMessage;
import ai.sagesource.sagent.llm.completion.chat.models.response.ChatLLMCompletionResponse;
import ai.sagesource.sagent.llm.function.FunctionToolDefinition;

import java.util.List;

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
}
