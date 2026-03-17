package ai.sagesource.llm.completion.chat;

import ai.sagesource.llm.client.LLMClient;
import ai.sagesource.llm.completion.LLMCompletion;
import ai.sagesource.llm.completion.LLMCompletionMessage;
import ai.sagesource.llm.completion.chat.models.messages.ChatLLMCompletionMessage;
import ai.sagesource.llm.completion.chat.models.response.ChatLLMCompletionResponse;

import java.util.List;

/**
 * Chat Completion Abstract Class
 * <p>
 * CLIENT: Actual LLMClient Implement
 *
 * @author: sage.xue
 * @time: 2026/3/16
 */
public abstract class ChatLLMCompletion<LLM_CLIENT extends LLMClient<?>> implements LLMCompletion {

    protected LLM_CLIENT llmClient;

    public ChatLLMCompletion(LLM_CLIENT llmClient) {
        this.llmClient = llmClient;
    }

    public abstract ChatLLMCompletionResponse thinking(List<ChatLLMCompletionMessage> messages, float temperature);

}
