package ai.sagesource.sagent.example.agent;

import ai.sagesource.sagent.agent.SimpleAgent;
import ai.sagesource.sagent.agent.config.AgentConfig;
import ai.sagesource.sagent.agent.llm.AgentLLMRequest;
import ai.sagesource.sagent.agent.llm.AgentLLMResponse;
import ai.sagesource.sagent.agent.llm.AgentStreamingCallback;
import ai.sagesource.sagent.agent.llm.AgentStreamingHandle;
import ai.sagesource.sagent.base.utils.DotEnvUtils;
import ai.sagesource.sagent.llm.client.LLMClientConfig;
import ai.sagesource.sagent.llm.openai.OpenAILLMChatCompletion;
import ai.sagesource.sagent.llm.openai.OpenAILLMClient;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * SimpleAgent异步调用示例
 * 演示异步调用和异步流式调用
 *
 * @author: sage.xue
 * @time: 2026/4/6
 */
public class SimpleAgentAsyncExample {

    public static void main(String[] args) throws Exception {
        // 加载环境变量
        Dotenv dotenv = DotEnvUtils.loadEnv();

        // 创建LLM客户端
        LLMClientConfig llmClientConfig = new LLMClientConfig();
        llmClientConfig.setBaseUrl(dotenv.get("LLM_BASE_URL"));
        llmClientConfig.setApiKey(dotenv.get("LLM_API_KEY"));
        llmClientConfig.setModel(dotenv.get("LLM_MODEL_ID"));
        llmClientConfig.setMaxToken(Long.parseLong(dotenv.get("LLM_MAX_TOKEN")));

        OpenAILLMClient openAILLMClient = new OpenAILLMClient(llmClientConfig).init();
        OpenAILLMChatCompletion llmCompletion = new OpenAILLMChatCompletion(openAILLMClient);

        // 创建Agent配置
        AgentConfig config = AgentConfig.builder()
                .name("AsyncAgent")
                .systemPromptContent("你是一个高效的AI助手，回答简短。")
                .llmCompletion(llmCompletion)
                .temperature(0.7f)
                .build();

        // 创建并初始化Agent
        SimpleAgent agent = new SimpleAgent(config);
        agent.initialize();

        // ========== 异步调用 ==========
        System.out.println("=== 异步调用 ===");
        
        CompletableFuture<AgentLLMResponse> future1 = agent.thinkAsync("什么是机器学习？");
        CompletableFuture<AgentLLMResponse> future2 = agent.thinkAsync(
            AgentLLMRequest.builder()
                .userInput("什么是深度学习？")
                .temperature(0.5f)
                .build()
        );

        // 等待两个异步调用完成
        CompletableFuture.allOf(future1, future2).join();

        System.out.println("回答1: " + future1.get().content());
        System.out.println("回答2: " + future2.get().content());

        // ========== 异步流式调用 ==========
        System.out.println("\n=== 异步流式调用 ===");
        
        StringBuilder asyncContentBuilder = new StringBuilder();
        
        AgentStreamingHandle asyncHandle = agent.thinkStreamAsync(
            "列举3个AI应用领域",
            new AgentStreamingCallback() {
                @Override
                public boolean onToken(AgentLLMResponse response) {
                    System.out.print(response.content());
                    asyncContentBuilder.append(response.content());
                    return true;
                }

                @Override
                public void onCompletion(AgentLLMResponse response) {
                    System.out.println("\n[异步流式完成]");
                }

                @Override
                public void onError(Throwable t) {
                    System.err.println("异步流式错误: " + t.getMessage());
                }
            }
        );

        // 等待异步流式输出完成
        asyncHandle.awaitCompletion(60, TimeUnit.SECONDS);
        System.out.println("完整异步内容: " + asyncContentBuilder.toString());
    }
}
