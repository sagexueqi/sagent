package ai.sagesource.llm.completion;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.UUID;

/**
 * CompletionMessage Abstract Class
 *
 * @author: sage.xue
 * @time: 2026/3/16
 */
@Getter
@Accessors(fluent = true)
@ToString
public abstract class CompletionMessage {
    /**
     * Message id, default is uuid
     */
    protected String id;
    /**
     * Message timestamp
     */
    protected long   timestamp;

    public CompletionMessage() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = Instant.now().toEpochMilli();
    }

    /**
     * Message Role
     *
     * @return
     */
    public abstract String role();
}
