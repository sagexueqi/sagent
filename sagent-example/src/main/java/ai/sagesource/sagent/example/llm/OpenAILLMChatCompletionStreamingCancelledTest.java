package ai.sagesource.sagent.example.llm;

import ai.sagesource.sagent.base.utils.DotEnvUtils;
import ai.sagesource.sagent.llm.client.LLMClientConfig;
import ai.sagesource.sagent.llm.completion.LLMCompletionStreamingCallback;
import ai.sagesource.sagent.llm.completion.LLMCompletionStreamingHandle;
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
public class OpenAILLMChatCompletionStreamingCancelledTest {


    public static void main(String[] args) throws Exception{
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
        LLMCompletionStreamingHandle handle = openAILLMChatCompletion.thinking_streaming(
                List.of(systemMessage, userMessage),
                null,
                0L,
                new LLMCompletionStreamingCallback<>() {
                    private int tokenCount = 0;

                    @Override
                    public boolean onToken(ChatLLMCompletionResponse llmCompletionResponse) {
                        // 方式1：获取token后，如果返回false，触发流中断
                        tokenCount++;
                        if (tokenCount > 10000) return false;

                        System.out.print(llmCompletionResponse.message().content());
                        return true;
                    }

                    @Override
                    public void onCompletion(ChatLLMCompletionResponse llmCompletionResponse) {
                        System.out.println("\n-------- onCompletion --------");
                        System.out.println(llmCompletionResponse.message().content());
                    }
                }
        );

        // 方式2：主动触发流中断
        handle.cancel();
    }
}
