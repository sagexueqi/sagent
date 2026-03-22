package ai.sagesource.llm.openai;

import ai.sagesource.sagent.base.utils.DotEnvUtils;
import ai.sagesource.sagent.llm.client.LLMClientConfig;
import ai.sagesource.sagent.llm.completion.chat.models.messages.ChatLLMCompletionSystemMessage;
import ai.sagesource.sagent.llm.completion.chat.models.messages.ChatLLMCompletionUserMessage;
import ai.sagesource.sagent.llm.completion.chat.models.response.ChatLLMCompletionResponse;
import ai.sagesource.sagent.llm.openai.OpenAILLMChatCompletion;
import ai.sagesource.sagent.llm.openai.OpenAILLMClient;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;

/**
 *
 * @author: sage.xue
 * @time: 2026/3/22
 */
public class OpenAILLMChatCompletionSampleTest {


    public static void main(String[] args) {
        Dotenv dotenv = DotEnvUtils.loadEnv();

        LLMClientConfig llmClientConfig = new LLMClientConfig();
        llmClientConfig.setBaseUrl(dotenv.get("LLM_BASE_URL"));
        llmClientConfig.setApiKey(dotenv.get("LLM_API_KEY"));
        llmClientConfig.setModel(dotenv.get("LLM_MODEL_ID"));
        llmClientConfig.setMaxToken(Long.parseLong(dotenv.get("LLM_MAX_TOKEN")));
        llmClientConfig.setConnectionTimeout(60);
        llmClientConfig.setReadTimeout(60);

        OpenAILLMClient openAILLMClient = new OpenAILLMClient(llmClientConfig).init();

        OpenAILLMChatCompletion openAILLMChatCompletion = new OpenAILLMChatCompletion(openAILLMClient);

        ChatLLMCompletionSystemMessage systemMessage = new ChatLLMCompletionSystemMessage();
        systemMessage.content("Your name is 'Sagent'");
        ChatLLMCompletionUserMessage userMessage = new ChatLLMCompletionUserMessage();
        userMessage.content("who are you?");
        ChatLLMCompletionResponse response = openAILLMChatCompletion.thinking(
                List.of(systemMessage, userMessage),
                null,
                0L
        );
        System.out.println(response.message().content());
    }
}
