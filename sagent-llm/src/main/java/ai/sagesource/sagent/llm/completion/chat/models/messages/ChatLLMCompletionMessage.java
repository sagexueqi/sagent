package ai.sagesource.sagent.llm.completion.chat.models.messages;

import ai.sagesource.sagent.llm.completion.LLMCompletionMessage;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * ChatCompletionMessage Base Class
 *
 * @author: sage.xue
 * @time: 2026/3/16
 */
@Getter
public abstract class ChatLLMCompletionMessage implements LLMCompletionMessage {

    /**
     * Chat Content
     */
    private final String              content;
    /**
     * additional info
     */
    private       Map<String, Object> additional;

    public ChatLLMCompletionMessage(String content, Map<String, Object> additional) {
        this.content = content;
        this.additional = additional;
    }

    public ChatLLMCompletionMessage(String content) {
        this.content = content;
    }

    @Override
    public String id() {
        return UUID.randomUUID().toString();
    }

    @Override
    public long timestamp() {
        return Instant.now().toEpochMilli();
    }
}
