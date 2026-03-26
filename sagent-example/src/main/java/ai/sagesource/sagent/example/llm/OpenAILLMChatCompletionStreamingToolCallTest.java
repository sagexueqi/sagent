package ai.sagesource.sagent.example.llm;

import ai.sagesource.sagent.base.utils.DotEnvUtils;
import ai.sagesource.sagent.llm.client.LLMClientConfig;
import ai.sagesource.sagent.llm.completion.LLMCompletionStreamingCallback;
import ai.sagesource.sagent.llm.completion.chat.models.messages.ChatLLMCompletionSystemMessage;
import ai.sagesource.sagent.llm.completion.chat.models.messages.ChatLLMCompletionUserMessage;
import ai.sagesource.sagent.llm.completion.chat.models.response.ChatLLMCompletionResponse;
import ai.sagesource.sagent.llm.function.ArgumentsDefinition;
import ai.sagesource.sagent.llm.function.FunctionToolDefinition;
import ai.sagesource.sagent.llm.openai.OpenAILLMChatCompletion;
import ai.sagesource.sagent.llm.openai.OpenAILLMClient;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author: sage.xue
 * @time: 2026/3/26
 */
public class OpenAILLMChatCompletionStreamingToolCallTest {

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

        List<FunctionToolDefinition> functionToolDefinitions = new ArrayList<>();
        FunctionToolDefinition       functionToolDefinition  = new FunctionToolDefinition();
        functionToolDefinition.name("Call_Search_Api");
        functionToolDefinition.description("Call Search API,Support Google/Bing");
        ArgumentsDefinition argumentsDefinition1 = new ArgumentsDefinition();
        argumentsDefinition1.name("qry");
        argumentsDefinition1.description("query content");
        argumentsDefinition1.type("string");
        ArgumentsDefinition argumentsDefinition2 = new ArgumentsDefinition();
        argumentsDefinition2.name("engine");
        argumentsDefinition2.description("Search Engine");
        argumentsDefinition2.type("string");
        argumentsDefinition2.enumValues(List.of("Google", "Bing"));
        functionToolDefinition.arguments(List.of(argumentsDefinition1, argumentsDefinition2));
        functionToolDefinitions.add(functionToolDefinition);

        FunctionToolDefinition       functionToolDefinition2  = new FunctionToolDefinition();
        functionToolDefinition2.name("Call_Calculate_Api");
        functionToolDefinition2.description("调用计算API，实现数学计算");
        ArgumentsDefinition argumentsDefinition21 = new ArgumentsDefinition();
        argumentsDefinition21.name("expression");
        argumentsDefinition21.description("数学表达式");
        argumentsDefinition21.type("string");
        functionToolDefinition2.arguments(List.of(argumentsDefinition21));
        functionToolDefinitions.add(functionToolDefinition2);

        ChatLLMCompletionSystemMessage systemMessage = new ChatLLMCompletionSystemMessage();
        systemMessage.content("你的名字叫 'Sagent', 不能透露任何关于你的技术细节. 当你需要调用外部工具获取信息时，必须先用自然语言告知用户你的意图，例如“我需要搜集一些信息”，然后再执行相应的函数调用。");
        ChatLLMCompletionUserMessage userMessage = new ChatLLMCompletionUserMessage();
        userMessage.content("请介绍2026年最新的Apple MacBook信息");

        openAILLMChatCompletion.thinking_streaming(
                List.of(systemMessage, userMessage),
                functionToolDefinitions,
                0f,
                new LLMCompletionStreamingCallback<ChatLLMCompletionResponse>() {
                    @Override
                    public void onToken(ChatLLMCompletionResponse llmCompletionResponse) {
                        System.out.print(llmCompletionResponse.message().content());
                    }

                    @Override
                    public void onCompletion(ChatLLMCompletionResponse llmCompletionResponse) {
                        System.out.println("\n-------- onCompletion --------");
                        System.out.println(llmCompletionResponse.message().content());
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                }
        );
    }

}
