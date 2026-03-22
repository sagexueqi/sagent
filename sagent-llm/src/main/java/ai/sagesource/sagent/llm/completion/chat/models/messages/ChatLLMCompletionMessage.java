package ai.sagesource.sagent.llm.completion.chat.models.messages;

import ai.sagesource.sagent.llm.completion.LLMCompletionMessage;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * ChatCompletionMessage Base Class
 *
 * @author: sage.xue
 * @time: 2026/3/16
 */
@Data
@Accessors(fluent = true)
public abstract class ChatLLMCompletionMessage implements LLMCompletionMessage {

    /**
     * Chat Content
     */
    private String              content;
    /**
     * additional info
     */
    private Map<String, Object> additional;

    @Override
    public String id() {
        return UUID.randomUUID().toString();
    }

    @Override
    public long timestamp() {
        return Instant.now().toEpochMilli();
    }
}
