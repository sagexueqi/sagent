package ai.sagesource.sagent.agent.config;

import ai.sagesource.sagent.llm.completion.LLMCompletion;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Map;

/**
 * Agent配置类
 * 封装Agent初始化所需的配置参数
 *
 * @author: sage.xue
 * @time: 2026/3/31
 */
@Getter
@Builder
public class AgentConfig {

    /**
     * Agent名称
     */
    private final String name;

    /**
     * System Prompt模板名称
     * 用于从PromptManager加载模板
     */
    private final String systemPromptTemplate;

    /**
     * System Prompt直接内容
     * 当不需要模板渲染时，可直接设置内容
     * 优先级：systemPromptContent > systemPromptTemplate
     */
    private final String systemPromptContent;

    /**
     * Prompt渲染参数
     */
    @Singular
    private final Map<String, Object> promptParameters;

    /**
     * Prompt配置
     * 用于创建PromptManager
     */
    private final PromptConfig promptConfig;

    /**
     * LLM Completion实例
     * 用于与大模型进行交互
     */
    private final LLMCompletion<?> llmCompletion;

    /**
     * 获取默认配置
     *
     * @param name Agent名称
     * @return 默认配置
     */
    public static AgentConfig defaultConfig(String name) {
        return AgentConfig.builder()
                .name(name)
                .promptConfig(PromptConfig.getDefault())
                .build();
    }

    /**
     * 使用模板创建配置
     *
     * @param name                 Agent名称
     * @param systemPromptTemplate System Prompt模板名称
     * @return 配置对象
     */
    public static AgentConfig withTemplate(String name, String systemPromptTemplate) {
        return AgentConfig.builder()
                .name(name)
                .systemPromptTemplate(systemPromptTemplate)
                .promptConfig(PromptConfig.getDefault())
                .build();
    }

    /**
     * 使用直接内容创建配置
     *
     * @param name                Agent名称
     * @param systemPromptContent System Prompt内容
     * @return 配置对象
     */
    public static AgentConfig withContent(String name, String systemPromptContent) {
        return AgentConfig.builder()
                .name(name)
                .systemPromptContent(systemPromptContent)
                .promptConfig(PromptConfig.getDefault())
                .build();
    }

    /**
     * 使用LLMCompletion创建配置
     *
     * @param name          Agent名称
     * @param llmCompletion LLM Completion实例
     * @return 配置对象
     */
    public static AgentConfig withLLMCompletion(String name, LLMCompletion<?> llmCompletion) {
        return AgentConfig.builder()
                .name(name)
                .llmCompletion(llmCompletion)
                .promptConfig(PromptConfig.getDefault())
                .build();
    }
}
