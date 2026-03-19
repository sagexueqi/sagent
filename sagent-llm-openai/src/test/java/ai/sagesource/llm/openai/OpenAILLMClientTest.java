package ai.sagesource.llm.openai;

import ai.sagesource.sagent.base.utils.DotEnvUtils;
import ai.sagesource.sagent.llm.client.LLMClientConfig;
import ai.sagesource.sagent.llm.openai.OpenAILLMClient;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessageFunctionToolCall;
import com.openai.models.chat.completions.ChatCompletionToolMessageParam;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Collection;

/**
 *
 * @author: sage.xue
 * @time: 2026/3/16
 */
public class OpenAILLMClientTest {

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

        // execution tools calling
        ChatCompletionCreateParams.Builder createParamsBuilder = ChatCompletionCreateParams.builder()
                .maxCompletionTokens(2048)
                .model("qwen-plus")
                .addTool(GetSdkQuality.class)
                .addTool(GetSdkScore.class)
                .addUserMessage("以下 SDK 的质量如何？评论者有何评价：OpenAI Java SDK、未知公司SDK. 如果你需要调用外部工具，请先返回内容：我需要调用一些工具获得数据。然后需要返回toolCalls");

        openAILLMClient.client().chat().completions().create(createParamsBuilder.build()).choices().stream()
                .map(ChatCompletion.Choice::message)
                // Add each assistant message onto the builder so that we keep track of the
                // conversation for asking a follow-up question later.
                .peek(createParamsBuilder::addMessage)
                .flatMap(message -> {
                    message.content().ifPresent(System.out::println);
                    return message.toolCalls().stream().flatMap(Collection::stream);
                })
                .forEach(toolCall -> {
                    Object result = callFunction(toolCall.asFunction().function());
                    // Add the tool call result to the conversation.
                    createParamsBuilder.addMessage(ChatCompletionToolMessageParam.builder()
                            .toolCallId(toolCall.asFunction().id())
                            .contentAsJson(result)
                            .build());
                });

        // Ask a follow-up question about the function call result.
        createParamsBuilder.addUserMessage("为什么这么说？");
        openAILLMClient.client().chat().completions().create(createParamsBuilder.build()).choices().stream()
                .flatMap(choice -> choice.message().content().stream())
                .forEach(System.out::println);
    }

    @JsonClassDescription("Gets the quality of the given SDK.")
    static class GetSdkQuality {
        @JsonPropertyDescription("The name of the SDK.")
        public String name;

        public SdkQuality execute() {
            return new SdkQuality(name, name.contains("OpenAI") ? "It's robust and polished!" : "*shrug*");
        }
    }

    static class SdkQuality {
        public String quality;

        public SdkQuality(String name, String evaluation) {
            quality = name + ": " + evaluation;
        }
    }

    @JsonClassDescription("Gets the review score (out of 10) for the named SDK.")
    static class GetSdkScore {
        public String name;

        public int execute() {
            return name.contains("OpenAI") ? 10 : 3;
        }
    }

    private static Object callFunction(ChatCompletionMessageFunctionToolCall.Function function) {
        switch (function.name()) {
            case "GetSdkQuality":
                return function.arguments(GetSdkQuality.class).execute();
            case "GetSdkScore":
                return function.arguments(GetSdkScore.class).execute();
            default:
                throw new IllegalArgumentException("Unknown function: " + function.name());
        }
    }
}
