package ai.sagesource.sagent.example.agent;

import ai.sagesource.sagent.agent.SimpleAgent;
import ai.sagesource.sagent.agent.config.AgentConfig;
import ai.sagesource.sagent.agent.llm.AgentLLMRequest;
import ai.sagesource.sagent.agent.llm.AgentLLMResponse;
import ai.sagesource.sagent.base.utils.DotEnvUtils;
import ai.sagesource.sagent.llm.client.LLMClientConfig;
import ai.sagesource.sagent.llm.openai.OpenAILLMChatCompletion;
import ai.sagesource.sagent.llm.openai.OpenAILLMClient;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * SimpleAgent同步调用示例
 * 演示基础的同步对话调用
 *
 * @author: sage.xue
 * @time: 2026/4/6
 */
public class SimpleAgentSyncExample {

    public static void main(String[] args) {
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

        // 创建Agent配置（使用直接内容设置System Prompt）
        AgentConfig config = AgentConfig.builder()
                .name("SyncAgent")
                .systemPromptContent("你是一个 helpful 的AI助手，回答简洁明了。")
                .llmCompletion(llmCompletion)
                .temperature(0.7f)
                .build();

        // 创建并初始化Agent
        SimpleAgent agent = new SimpleAgent(config);
        agent.initialize();

        // 方式1: 简单字符串调用
        System.out.println("=== 方式1: 简单字符串调用 ===");
        AgentLLMResponse response1 = agent.think("你好，请介绍一下你自己");
        System.out.println("Agent回复: " + response1.content());

        // 方式2: 使用AgentLLMRequest调用（支持更多参数）
        System.out.println("\n=== 方式2: 使用AgentLLMRequest调用 ===");
        AgentLLMRequest request = AgentLLMRequest.builder()
                .userInput("今天的天气怎么样？（假设）")
                .temperature(0.5f)  // 覆盖默认temperature
                .build();
        AgentLLMResponse response2 = agent.think(request);
        System.out.println("Agent回复: " + response2.content());
    }
}
