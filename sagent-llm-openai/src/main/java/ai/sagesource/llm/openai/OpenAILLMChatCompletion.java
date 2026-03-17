package ai.sagesource.llm.openai;

import ai.sagesource.llm.completion.chat.ChatLLMCompletion;
import ai.sagesource.llm.completion.chat.models.messages.*;
import ai.sagesource.llm.completion.chat.models.response.ChatLLMCompletionResponse;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import java.util.List;

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
    public ChatLLMCompletionResponse thinking(List<ChatLLMCompletionMessage> messages, float temperature) {
        ChatLLMCompletionResponse response = new ChatLLMCompletionResponse();

        OpenAIClient               openAIClient = llmClient.client();
        ChatCompletionCreateParams params       = this.chatCompletionCreateParams(messages, temperature);



        return null;
    }


    private ChatCompletionCreateParams chatCompletionCreateParams(List<ChatLLMCompletionMessage> messages, float temperature) {
        ChatCompletionCreateParams.Builder builder = ChatCompletionCreateParams.builder();
        builder.model(this.llmClient.model())
                .temperature(temperature)
                .maxCompletionTokens(this.llmClient.maxToken());

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
