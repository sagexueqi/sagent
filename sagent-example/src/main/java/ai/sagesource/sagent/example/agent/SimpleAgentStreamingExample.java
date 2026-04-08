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

import java.util.concurrent.TimeUnit;

/**
 * SimpleAgent流式调用示例
 * 演示流式输出，实时获取生成的内容
 *
 * @author: sage.xue
 * @time: 2026/4/6
 */
public class SimpleAgentStreamingExample {

    public static void main(String[] args) throws InterruptedException {
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
                .name("StreamingAgent")
                .systemPromptContent("你是一个擅长写诗的AI助手。")
                .llmCompletion(llmCompletion)
                .temperature(0.8f)
                .build();

        // 创建并初始化Agent
        SimpleAgent agent = new SimpleAgent(config);
        agent.initialize();

        // 方式1: 简单字符串流式调用
        System.out.println("=== 方式1: 简单字符串流式调用 ===");
        StringBuilder contentBuilder1 = new StringBuilder();
        
        AgentStreamingHandle handle1 = agent.thinkStreaming(
            "写一首关于春天的短诗",
            new AgentStreamingCallback() {
                @Override
                public boolean onToken(AgentLLMResponse response) {
                    // 实时输出每个token
                    System.out.print(response.content());
                    contentBuilder1.append(response.content());
                    return true;  // 返回true继续，返回false中断流
                }

                @Override
                public void onCompletion(AgentLLMResponse response) {
                    System.out.println("\n\n[流式输出完成]");
                    System.out.println("完整内容: " + contentBuilder1.toString());
                }

                @Override
                public void onError(Throwable t) {
                    System.err.println("流式输出出错: " + t.getMessage());
                }
            }
        );

        // 等待流式输出完成
        handle1.awaitCompletion(60, TimeUnit.SECONDS);

        // 方式2: 使用AgentLLMRequest流式调用
        System.out.println("\n=== 方式2: 使用AgentLLMRequest流式调用 ===");
        
        AgentLLMRequest request = AgentLLMRequest.builder()
                .userInput("用一句话形容夏天")
                .temperature(0.9f)
                .build();

        StringBuilder contentBuilder2 = new StringBuilder();
        
        AgentStreamingHandle handle2 = agent.thinkStreaming(
            request,
            new AgentStreamingCallback() {
                @Override
                public boolean onToken(AgentLLMResponse response) {
                    System.out.print(response.content());
                    contentBuilder2.append(response.content());
                    return true;
                }

                @Override
                public void onCompletion(AgentLLMResponse response) {
                    System.out.println("\n[完成]");
                }

                @Override
                public void onError(Throwable t) {
                    System.err.println("错误: " + t.getMessage());
                }
            }
        );

        handle2.awaitCompletion(60, TimeUnit.SECONDS);
    }
}
