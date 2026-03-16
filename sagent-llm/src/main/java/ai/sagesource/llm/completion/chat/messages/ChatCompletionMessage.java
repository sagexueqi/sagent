package ai.sagesource.llm.completion.chat.messages;

import ai.sagesource.llm.completion.CompletionMessage;
import lombok.Getter;

import java.util.Map;

/**
 * ChatCompletionMessage Base Class
 *
 * @author: sage.xue
 * @time: 2026/3/16
 */
@Getter
public abstract class ChatCompletionMessage extends CompletionMessage {

    /**
     * Chat Content
     */
    private String              content;
    /**
     * additional info
     */
    private Map<String, Object> additional;

    public ChatCompletionMessage(String content, Map<String, Object> additional) {
        this.content = content;
        this.additional = additional;
    }

    public ChatCompletionMessage(String content) {
        this.content = content;
    }
}
