package ai.sagesource.sagent.agent;

import ai.sagesource.sagent.agent.config.AgentConfig;
import ai.sagesource.sagent.agent.config.PromptConfig;
import ai.sagesource.sagent.agent.prompt.PromptManager;
import ai.sagesource.sagent.agent.prompt.PromptRenderContext;
import ai.sagesource.sagent.llm.completion.LLMCompletion;
import ai.sagesource.sagent.llm.completion.chat.models.messages.ChatLLMCompletionMessage;
import ai.sagesource.sagent.llm.completion.chat.models.messages.ChatLLMCompletionSystemMessage;
import ai.sagesource.sagent.llm.completion.chat.models.messages.ChatLLMCompletionUserMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Optional;

/**
 * Agent基类
 * 提供Agent的基础实现，包括System Prompt的获取逻辑
 *
 * @author: sage.xue
 * @time: 2026/3/31
 */
@Slf4j
public abstract class AbstractAgent implements Agent {

    protected final AgentConfig   config;
    protected final PromptManager promptManager;

    /**
     * LLM Completion实例
     * 供子类与大模型交互使用
     */
    protected final LLMCompletion<?> llmCompletion;

    @Getter
    private volatile boolean initialized = false;

    /**
     * System Prompt缓存
     * 初始化时加载，子类可通过getSystemPrompt()访问
     */
    protected String systemPrompt;

    /**
     * 使用配置创建Agent
     *
     * @param config Agent配置
     */
    protected AbstractAgent(AgentConfig config) {
        this.config = Optional.ofNullable(config)
                .orElseThrow(() -> new IllegalArgumentException("AgentConfig cannot be null"));
        this.promptManager = createPromptManager(config);
        this.llmCompletion = config.getLlmCompletion();
        log.debug("AbstractAgent created with name: {}", config.getName());
    }

    /**
     * 使用默认配置创建Agent
     *
     * @param name Agent名称
     */
    protected AbstractAgent(String name) {
        this(AgentConfig.defaultConfig(name));
    }

    @Override
    public String name() {
        return config.getName();
    }

    @Override
    public void initialize() {
        if (initialized) {
            log.warn("Agent [{}] is already initialized", name());
            return;
        }

        synchronized (this) {
            if (initialized) {
                return;
            }

            // 加载System Prompt
            this.systemPrompt = loadSystemPrompt();
            log.info("Agent [{}] loaded system prompt, length: {}",
                    name(),
                    systemPrompt != null ? systemPrompt.length() : 0);

            // 执行子类的初始化逻辑
            doInitialize();

            this.initialized = true;
            log.info("Agent [{}] initialized successfully", name());
        }
    }

    /**
     * 获取System Prompt
     * 供子类访问初始化后的System Prompt
     *
     * @return System Prompt内容
     */
    protected String getSystemPrompt() {
        return systemPrompt;
    }

    /**
     * 加载System Prompt
     * 优先级：直接内容 > 模板渲染
     *
     * @return System Prompt内容
     */
    protected String loadSystemPrompt() {
        // 优先使用直接内容
        if (config.getSystemPromptContent() != null && !config.getSystemPromptContent().isEmpty()) {
            return config.getSystemPromptContent();
        }

        // 使用模板渲染
        if (config.getSystemPromptTemplate() != null && !config.getSystemPromptTemplate().isEmpty()) {
            return promptManager.render(
                    config.getSystemPromptTemplate(),
                    PromptRenderContext.of(
                            Optional.ofNullable(config.getPromptParameters())
                                    .orElse(Collections.emptyMap())
                    )
            );
        }

        // 默认返回空字符串
        log.warn("Agent [{}] has no system prompt configured", name());
        return "";
    }

    /**
     * 创建PromptManager
     *
     * @param config Agent配置
     * @return PromptManager实例
     */
    protected PromptManager createPromptManager(AgentConfig config) {
        PromptConfig promptConfig = Optional.ofNullable(config.getPromptConfig())
                .orElseGet(PromptConfig::getDefault);
        return new PromptManager(promptConfig);
    }

    /**
     * 子类初始化钩子
     * 子类可重写此方法执行额外的初始化逻辑
     */
    protected abstract void doInitialize();

    /**
     * 创建System Message
     * 使用已加载的System Prompt
     *
     * @return System消息
     */
    protected ChatLLMCompletionMessage createSystemMessage() {
        ChatLLMCompletionSystemMessage message = new ChatLLMCompletionSystemMessage();
        message.content(getSystemPrompt());
        return message;
    }

    /**
     * 创建User Message
     *
     * @param content 用户输入内容
     * @return User消息
     */
    protected ChatLLMCompletionMessage createUserMessage(String content) {
        ChatLLMCompletionUserMessage message = new ChatLLMCompletionUserMessage();
        message.content(content);
        return message;
    }
}
