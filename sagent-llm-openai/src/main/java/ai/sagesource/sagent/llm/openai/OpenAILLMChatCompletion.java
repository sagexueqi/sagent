package ai.sagesource.sagent.llm.openai;

import ai.sagesource.sagent.llm.completion.chat.ChatLLMCompletion;
import ai.sagesource.sagent.llm.completion.chat.models.messages.*;
import ai.sagesource.sagent.llm.completion.chat.models.response.ChatLLMCompletionResponse;
import ai.sagesource.sagent.llm.function.FunctionToolDefinition;
import com.openai.client.OpenAIClient;
import com.openai.core.JsonValue;
import com.openai.models.FunctionDefinition;
import com.openai.models.FunctionParameters;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionFunctionTool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        ChatLLMCompletionResponse response = new ChatLLMCompletionResponse();

        OpenAIClient               openAIClient = llmClient.client();
        ChatCompletionCreateParams params       = this.chatCompletionCreateParams(messages, functions, temperature);


        return null;
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
                // Parameter Definition
                FunctionParameters.Builder parameterBuilder = FunctionParameters.builder();
                parameterBuilder.putAdditionalProperty("type", JsonValue.from("object"));
                Map<String, Object> properties = new HashMap<>();
                if (function.arguments() != null && !function.arguments().isEmpty()) {
                    function.arguments().forEach((argument) -> {
                        Map<String, Object> argumentProperties = new HashMap<>();
                        // type definition: all string
                        argumentProperties.put("type", "string");
                        // description definition
                        argumentProperties.put("description", argument.description());
                        // enum values
                        if (argument.enumValues() != null && !argument.enumValues().isEmpty()) {
                            argumentProperties.put("enum", argument.enumValues());
                        }

                        properties.put(argument.name(), argumentProperties);
                    });
                }
                parameterBuilder.putAdditionalProperty("properties", JsonValue.from(properties));
                parameterBuilder.putAdditionalProperty("required", JsonValue.from(function.requiredArguments()));

                // Function Definition
                FunctionDefinition functionDefinition = FunctionDefinition.builder()
                        .name(function.name())
                        .description(function.description())
                        .parameters(parameterBuilder.build())
                        .build();

                // Tool Definition
                builder.addTool(
                        ChatCompletionFunctionTool.builder().function(functionDefinition).build()
                );
            });
        }

        // Add Message
        messages.forEach(message -> {
            if (message instanceof ChatLLMCompletionAssistantMessage) {
                builder.addAssistantMessage(message.getContent());
            } else if (message instanceof ChatLLMCompletionDeveloperMessage) {
                builder.addDeveloperMessage(message.getContent());
            } else if (message instanceof ChatLLMCompletionSystemMessage) {
                builder.addSystemMessage(message.getContent());
            } else if (message instanceof ChatLLMCompletionUserMessage) {
                builder.addUserMessage(message.getContent());
            }

        });
        return builder.build();
    }

}
