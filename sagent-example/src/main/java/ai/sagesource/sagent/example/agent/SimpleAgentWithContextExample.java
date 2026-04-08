package ai.sagesource.sagent.example.agent;

import ai.sagesource.sagent.agent.SimpleAgent;
import ai.sagesource.sagent.agent.config.AgentConfig;
import ai.sagesource.sagent.agent.context.builder.MemoryContextBuilder;
import ai.sagesource.sagent.agent.llm.AgentLLMRequest;
import ai.sagesource.sagent.agent.llm.AgentLLMResponse;
import ai.sagesource.sagent.base.utils.DotEnvUtils;
import ai.sagesource.sagent.llm.client.LLMClientConfig;
import ai.sagesource.sagent.llm.openai.OpenAILLMChatCompletion;
import ai.sagesource.sagent.llm.openai.OpenAILLMClient;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.UUID;

/**
 * SimpleAgent带上下文对话示例
 * 演示使用MemoryContextBuilder实现多轮对话记忆
 *
 * @author: sage.xue
 * @time: 2026/4/6
 */
public class SimpleAgentWithContextExample {

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

        // 创建MemoryContextBuilder实现对话记忆
        MemoryContextBuilder contextBuilder = new MemoryContextBuilder();

        // 创建Agent配置
        AgentConfig config = AgentConfig.builder()
                .name("ContextAgent")
                .systemPromptContent("你是一个有记忆的AI助手，能够记住之前的对话内容。")
                .llmCompletion(llmCompletion)
                .temperature(0.7f)
                .contextBuilder(contextBuilder)  // 使用MemoryContextBuilder
                .build();

        // 创建并初始化Agent
        SimpleAgent agent = new SimpleAgent(config);
        agent.initialize();

        // 生成一个上下文ID，用于标识这次对话
        String contextId = UUID.randomUUID().toString();
        System.out.println("对话上下文ID: " + contextId);

        // ========== 第一轮对话 ==========
        System.out.println("\n=== 第一轮对话 ===");
        System.out.println("用户: 你好，我叫小明");
        
        AgentLLMRequest request1 = AgentLLMRequest.builder()
                .contextId(contextId)
                .userInput("你好，我叫小明")
                .build();
        AgentLLMResponse response1 = agent.think(request1);
        System.out.println("Agent: " + response1.content());

        // ========== 第二轮对话（Agent应该记得用户叫小明）==========
        System.out.println("\n=== 第二轮对话 ===");
        System.out.println("用户: 我叫什么名字？");
        
        AgentLLMRequest request2 = AgentLLMRequest.builder()
                .contextId(contextId)
                .userInput("我叫什么名字？")
                .build();
        AgentLLMResponse response2 = agent.think(request2);
        System.out.println("Agent: " + response2.content());

        // ========== 第三轮对话（继续上下文）==========
        System.out.println("\n=== 第三轮对话 ===");
        System.out.println("用户: 我喜欢吃苹果");
        
        AgentLLMRequest request3 = AgentLLMRequest.builder()
                .contextId(contextId)
                .userInput("我喜欢吃苹果")
                .build();
        AgentLLMResponse response3 = agent.think(request3);
        System.out.println("Agent: " + response3.content());

        // ========== 第四轮对话（验证记忆）==========
        System.out.println("\n=== 第四轮对话 ===");
        System.out.println("用户: 总结下我们刚才聊了什么？");
        
        AgentLLMRequest request4 = AgentLLMRequest.builder()
                .contextId(contextId)
                .userInput("总结下我们刚才聊了什么？")
                .build();
        AgentLLMResponse response4 = agent.think(request4);
        System.out.println("Agent: " + response4.content());

        // 查看当前上下文的对话轮数
        System.out.println("\n=== 对话统计 ===");
        System.out.println("当前上下文的对话轮数: " + 
            contextBuilder.getContext(contextId).historyMessages().size() / 2);

        // 清除上下文
        contextBuilder.clear(contextId);
        System.out.println("上下文已清除");
    }
}
