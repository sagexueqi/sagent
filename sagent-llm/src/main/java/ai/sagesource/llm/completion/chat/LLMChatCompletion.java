package ai.sagesource.llm.completion.chat;

import ai.sagesource.llm.client.LLMClient;
import ai.sagesource.llm.completion.LLMCompletion;

/**
 * Chat Completion Abstract Class
 * <p>
 * CLIENT: Actual LLMClient Implement
 *
 * @author: sage.xue
 * @time: 2026/3/16
 */
public abstract class LLMChatCompletion<T extends LLMClient<?>> implements LLMCompletion {
}
