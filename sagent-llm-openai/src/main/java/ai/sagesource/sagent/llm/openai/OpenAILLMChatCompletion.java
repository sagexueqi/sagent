package ai.sagesource.sagent.llm.openai;

import ai.sagesource.sagent.llm.completion.chat.ChatLLMCompletion;
import ai.sagesource.sagent.llm.completion.chat.models.messages.*;
import ai.sagesource.sagent.llm.completion.chat.models.response.ChatLLMCompletionResponse;
import ai.sagesource.sagent.llm.completion.chat.models.response.ChatLLMCompletionResponseToolCall;
import ai.sagesource.sagent.llm.function.FunctionToolDefinition;
import ai.sagesource.sagent.llm.openai.support.OpenAIChatCompletionMessageToolCallSupport;
import ai.sagesource.sagent.llm.openai.support.OpenAIFunctionDefinitionSupport;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.*;

import java.util.*;
import java.util.stream.Collectors;

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

        List<ChatLLMCompletionResponseToolCall> toolCalls = new ArrayList<>();
        openAIClient.chat().completions().create(params).choices().stream()
                .map(ChatCompletion.Choice::message)
                .flatMap(message -> {
                    // sync response has content
                    message.content().ifPresent(assistantMessage::content);
                    // return tool call stream
                    return message.toolCalls().stream().flatMap(Collection::stream);
                }).forEach(chatCompletionMessageToolCall -> {
                    toolCalls.add(
                            OpenAIChatCompletionMessageToolCallSupport.to(chatCompletionMessageToolCall)
                    );
                });
        assistantMessage.toolCalls(toolCalls);
        return response;
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

}
