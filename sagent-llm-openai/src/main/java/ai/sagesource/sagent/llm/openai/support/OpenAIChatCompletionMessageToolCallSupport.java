package ai.sagesource.sagent.llm.openai.support;

import ai.sagesource.sagent.llm.completion.chat.models.response.ChatLLMCompletionResponseFunctionToolCall;
import ai.sagesource.sagent.llm.completion.chat.models.response.ChatLLMCompletionResponseToolCall;
import com.openai.core.JsonValue;
import com.openai.models.chat.completions.ChatCompletionMessageFunctionToolCall;
import com.openai.models.chat.completions.ChatCompletionMessageToolCall;

import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author: sage.xue
 * @time: 2026/3/22
 */
public class OpenAIChatCompletionMessageToolCallSupport {

    public static ChatLLMCompletionResponseToolCall to(ChatCompletionMessageToolCall chatCompletionMessageToolCall) {
        // process tool call
        ChatLLMCompletionResponseToolCall chatLLMCompletionResponseToolCall = new ChatLLMCompletionResponseToolCall();

        ChatLLMCompletionResponseFunctionToolCall chatLLMCompletionResponseFunctionToolCall = new ChatLLMCompletionResponseFunctionToolCall();
        chatLLMCompletionResponseFunctionToolCall.id(chatCompletionMessageToolCall.asFunction().id());
        chatLLMCompletionResponseFunctionToolCall.functionName(chatCompletionMessageToolCall.asFunction().function().name());
        Map<Object, Object> argsMap = chatCompletionMessageToolCall.asFunction().function().arguments(Map.class);
        if (argsMap != null) {
            chatLLMCompletionResponseFunctionToolCall.arguments(argsMap.entrySet().stream().collect(
                    Collectors.toMap(
                            entry -> entry.getKey().toString(),
                            entry -> entry.getValue().toString()
                    )
            ));
        }
        chatLLMCompletionResponseToolCall.function(chatLLMCompletionResponseFunctionToolCall);
        return chatLLMCompletionResponseToolCall;
    }

    public static ChatCompletionMessageToolCall from(ChatLLMCompletionResponseToolCall toolCall) {
        ChatLLMCompletionResponseFunctionToolCall toolCallFunction = toolCall.function();
        ChatCompletionMessageFunctionToolCall chatCompletionMessageFunctionToolCall = ChatCompletionMessageFunctionToolCall.builder()
                .id(toolCallFunction.id())
                .function(ChatCompletionMessageFunctionToolCall.Function.builder()
                        .name(toolCallFunction.functionName())
                        .arguments(JsonValue.from(toolCallFunction.arguments()))
                        .build())
                .build();
        return ChatCompletionMessageToolCall.ofFunction(chatCompletionMessageFunctionToolCall);
    }
}
