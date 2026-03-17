package ai.sagesource.llm.completion;

import lombok.experimental.Accessors;

/**
 * Completion Sent Message Interface
 *
 * @author: sage.xue
 * @time: 2026/3/16
 */
@Accessors(fluent = true)
public interface LLMCompletionMessage {

    /**
     * Message id, default is uuid
     */
    String id();

    /**
     * Message timestamp
     */
    long timestamp();

    /**
     * Message Role
     *
     * @return
     */
    String role();
}
