package ai.sagesource.sagent.example.llm;

import ai.sagesource.sagent.base.utils.DotEnvUtils;
import ai.sagesource.sagent.llm.client.LLMClientConfig;
import ai.sagesource.sagent.llm.openai.OpenAILLMClient;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.stream.Collectors;

/**
 *
 * @author: sage.xue
 * @time: 2026/3/19
 */
public class OpenAIClientSampleExample {

    public static void main(String[] args) {
        Dotenv dotenv = DotEnvUtils.loadEnv();

        LLMClientConfig llmClientConfig = new LLMClientConfig();
        llmClientConfig.setBaseUrl(dotenv.get("LLM_BASE_URL"));
        llmClientConfig.setApiKey(dotenv.get("LLM_API_KEY"));
        llmClientConfig.setModel(dotenv.get("LLM_MODEL_ID"));
        llmClientConfig.setMaxToken(4096L);
        llmClientConfig.setConnectionTimeout(60);
        llmClientConfig.setReadTimeout(60);

        OpenAILLMClient openAILLMClient = new OpenAILLMClient(llmClientConfig).init();

        ChatCompletionCreateParams.Builder createParamsBuilder = ChatCompletionCreateParams.builder()
                .maxCompletionTokens(2048)
                .model("qwen-plus");
        createParamsBuilder.addUserMessage("Who Are You?");

        String content = openAILLMClient.client().chat().completions()
                .create(createParamsBuilder.build())
                .choices().stream()
                .flatMap(choice -> choice.message().content().stream())
                .collect(Collectors.joining("\n"));
        System.out.println(content);
    }

}
