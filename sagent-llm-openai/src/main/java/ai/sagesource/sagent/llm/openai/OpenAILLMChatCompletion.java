package ai.sagesource.sagent.llm.openai;

import ai.sagesource.sagent.llm.completion.LLMCompletionStreamingCallback;
import ai.sagesource.sagent.llm.completion.chat.ChatLLMCompletion;
import ai.sagesource.sagent.llm.completion.chat.models.messages.*;
import ai.sagesource.sagent.llm.completion.chat.models.response.ChatLLMCompletionResponse;
import ai.sagesource.sagent.llm.completion.chat.models.response.ChatLLMCompletionResponseToolCall;
import ai.sagesource.sagent.llm.function.FunctionToolDefinition;
import ai.sagesource.sagent.llm.openai.support.OpenAIChatCompletionMessageToolCallSupport;
import ai.sagesource.sagent.llm.openai.support.OpenAIFunctionDefinitionSupport;
import com.openai.client.OpenAIClient;
import com.openai.core.http.StreamResponse;
import com.openai.helpers.ChatCompletionAccumulator;
import com.openai.models.chat.completions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implement OpenAI Chat Completion
 *
 * @author: sage.xue
 * @time: 2026/3/16
 */
public class OpenAILLMChatCompletion extends ChatLLMCompletion<OpenAILLMClient> {

    public OpenAILLMChatCompletion(OpenAILLMClient llmClient) {
        super(llmClient);
    }

    @Override
    public ChatLLMCompletionResponse thinking(List<ChatLLMCompletionMessage> messages,
                                              List<FunctionToolDefinition> functions,
                                              float temperature) {
        ChatLLMCompletionResponse         response         = new ChatLLMCompletionResponse();
        ChatLLMCompletionAssistantMessage assistantMessage = new ChatLLMCompletionAssistantMessage();
        response.message(assistantMessage);

        // get openai client
        OpenAIClient openAIClient = llmClient.client();
        // build chat param
        ChatCompletionCreateParams params = this.chatCompletionCreateParams(messages, functions, temperature);
        // call open-ai
        ChatCompletion chatCompletion = openAIClient.chat().completions().create(params);
        // build response
        return toolCallsResponseBuild(chatCompletion);
    }

    @Override
    public void thinking_streaming(List<ChatLLMCompletionMessage> messages,
                                   List<FunctionToolDefinition> functions,
                                   float temperature,
                                   LLMCompletionStreamingCallback<ChatLLMCompletionResponse> streamingCallback) {
        // get openai client
        OpenAIClient openAIClient = llmClient.client();
        // build chat param
        ChatCompletionCreateParams params = this.chatCompletionCreateParams(messages, functions, temperature);

        // create open-ai accumulator
        ChatCompletionAccumulator accumulator = ChatCompletionAccumulator.create();

        // streaming request
        try (StreamResponse<ChatCompletionChunk> streamResponse =
                     openAIClient.chat().completions().createStreaming(params)) {
            Stream<ChatCompletionChunk> stream = streamResponse.stream();
            stream
                    // accumulate chunk
                    .peek(accumulator::accumulate)
                    .flatMap(completion -> completion.choices().stream())
                    .flatMap(choice -> choice.delta().content().stream())
                    .forEach(content -> {
                        ChatLLMCompletionResponse         response         = new ChatLLMCompletionResponse();
                        ChatLLMCompletionAssistantMessage assistantMessage = new ChatLLMCompletionAssistantMessage();
                        response.message(assistantMessage);
                        assistantMessage.content(content);
                        streamingCallback.onToken(response);
                    });
        }

        // streaming ending
        ChatCompletion chatCompletion = accumulator.chatCompletion();
        streamingCallback.onCompletion(toolCallsResponseBuild(chatCompletion));
    }

    // init chat completion create params
    private ChatCompletionCreateParams chatCompletionCreateParams(List<ChatLLMCompletionMessage> messages,
                                                                  List<FunctionToolDefinition> functions,
                                                                  float temperature) {
        ChatCompletionCreateParams.Builder builder = ChatCompletionCreateParams.builder();
        builder.model(this.llmClient.model())
                .temperature(temperature)
                .maxCompletionTokens(this.llmClient.maxToken());

        // Add Tool Definition
        if (functions != null && !functions.isEmpty()) {
            functions.forEach(function -> {
                // Tool Definition
                builder.addTool(
                        ChatCompletionFunctionTool.builder()
                                .function(OpenAIFunctionDefinitionSupport.from(function))
                                .build()
                );
            });
        }

        // Add Message
        messages.forEach(message -> {
            if (message instanceof ChatLLMCompletionAssistantMessage assistantMessage) {
                builder.addAssistantMessage(message.content());
                ChatCompletionAssistantMessageParam.Builder messageParamBuilder = ChatCompletionAssistantMessageParam.builder();

                // need support tool call info
                if (assistantMessage.toolCalls() != null && !assistantMessage.toolCalls().isEmpty()) {
                    messageParamBuilder.toolCalls(
                            assistantMessage.toolCalls().stream()
                                    .map(OpenAIChatCompletionMessageToolCallSupport::from)
                                    .collect(Collectors.toList())
                    );
                }

                builder.addMessage(messageParamBuilder
                        .content(message.content())
                        .build()
                );
            } else if (message instanceof ChatLLMCompletionDeveloperMessage) {
                builder.addDeveloperMessage(message.content());
            } else if (message instanceof ChatLLMCompletionSystemMessage) {
                builder.addSystemMessage(message.content());
            } else if (message instanceof ChatLLMCompletionUserMessage) {
                builder.addUserMessage(message.content());
            }

        });
        return builder.build();
    }

    // build tool calls response
    private ChatLLMCompletionResponse toolCallsResponseBuild(ChatCompletion chatCompletion) {
        ChatLLMCompletionResponse         response         = new ChatLLMCompletionResponse();
        ChatLLMCompletionAssistantMessage assistantMessage = new ChatLLMCompletionAssistantMessage();
        response.message(assistantMessage);
        List<ChatLLMCompletionResponseToolCall> toolCalls = new ArrayList<>();
        boolean hasToolCallFinish = chatCompletion.choices().stream()
                .anyMatch(choice -> "tool_calls".equalsIgnoreCase(choice.finishReason().asString()));

        chatCompletion.choices().stream()
                .map(ChatCompletion.Choice::message)
                .flatMap(message -> {
                    message.content().ifPresent(assistantMessage::content);
                    return message.toolCalls().stream().flatMap(Collection::stream);
                }).forEach(chatCompletionMessageToolCall -> {
                    toolCalls.add(
                            OpenAIChatCompletionMessageToolCallSupport.to(chatCompletionMessageToolCall)
                    );
                });
        assistantMessage.toolCalls(toolCalls);
        response.toolCalls(hasToolCallFinish);

        return response;
    }

}
