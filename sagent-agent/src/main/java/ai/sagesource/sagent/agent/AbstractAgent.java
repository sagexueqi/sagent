package ai.sagesource.sagent.agent;

import ai.sagesource.sagent.agent.config.AgentConfig;
import ai.sagesource.sagent.agent.config.PromptConfig;
import ai.sagesource.sagent.agent.context.AgentContext;
import ai.sagesource.sagent.agent.context.builder.ContextBuilder;
import ai.sagesource.sagent.agent.context.builder.SimpleContextBuilder;
import ai.sagesource.sagent.agent.llm.AgentLLMRequest;
import ai.sagesource.sagent.agent.llm.AgentLLMResponse;
import ai.sagesource.sagent.agent.llm.AgentStreamingCallback;
import ai.sagesource.sagent.agent.llm.AgentStreamingHandle;
import ai.sagesource.sagent.agent.prompt.PromptManager;
import ai.sagesource.sagent.agent.prompt.PromptRenderContext;
import ai.sagesource.sagent.llm.completion.LLMCompletion;
import ai.sagesource.sagent.llm.completion.chat.models.response.ChatLLMCompletionResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
    protected final LLMCompletion<ChatLLMCompletionResponse> llmCompletion;

    /**
     * Context构建器
     */
    protected final ContextBuilder contextBuilder;

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
        this.contextBuilder = Optional.ofNullable(config.getContextBuilder())
                .orElseGet(SimpleContextBuilder::new);
        log.debug("AbstractAgent created with name: {}, contextBuilder: {}",
                config.getName(), this.contextBuilder.name());
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

    // ========== Context管理 ==========

    /**
     * 构建运行Context
     * Agent在运行前调用此方法构建上下文
     *
     * @param contextId 上下文标识，可为null
     * @return AgentContext实例
     */
    protected AgentContext buildContext(String contextId) {
        AgentContext context = contextBuilder.build(contextId);
        log.debug("Agent [{}] built context with id: {}", name(), context.contextId());
        return context;
    }

    /**
     * 保存Context
     * Agent在运行结束后调用此方法保存上下文状态
     *
     * @param context 需要保存的上下文
     */
    protected void saveContext(AgentContext context) {
        if (context != null) {
            contextBuilder.save(context);
            log.debug("Agent [{}] saved context with id: {}", name(), context.contextId());
        }
    }

    /**
     * 清除指定上下文
     *
     * @param contextId 上下文标识
     */
    protected void clearContext(String contextId) {
        contextBuilder.clear(contextId);
        log.debug("Agent [{}] cleared context with id: {}", name(), contextId);
    }

    /**
     * 从Request中获取contextId
     */
    protected String extractContextId(AgentLLMRequest request) {
        return request != null ? request.contextId() : null;
    }

    // ========== 同步调用 ==========

    /**
     * 同步调用Agent进行思考
     * 自动组装System Prompt和User Message，由具体子类实现LLM调用细节
     *
     * @param userInput 用户输入
     * @return Agent层响应
     */
    public abstract AgentLLMResponse think(String userInput);

    /**
     * 同步调用Agent进行思考（带完整请求参数）
     *
     * @param request Agent层请求参数
     * @return Agent层响应
     */
    public abstract AgentLLMResponse think(AgentLLMRequest request);

    // ========== 异步调用 ==========

    /**
     * 异步调用Agent进行思考
     *
     * @param userInput 用户输入
     * @return 异步Agent层响应
     */
    public abstract CompletableFuture<AgentLLMResponse> thinkAsync(String userInput);

    /**
     * 异步调用Agent进行思考（带完整请求参数）
     *
     * @param request Agent层请求参数
     * @return 异步Agent层响应
     */
    public abstract CompletableFuture<AgentLLMResponse> thinkAsync(AgentLLMRequest request);

    // ========== 流式调用 ==========

    /**
     * 流式调用Agent进行思考
     *
     * @param userInput 用户输入
     * @param callback  Agent层流式回调
     * @return Agent层流式控制句柄
     */
    public abstract AgentStreamingHandle thinkStreaming(String userInput, AgentStreamingCallback callback);

    /**
     * 流式调用Agent进行思考（带完整请求参数）
     *
     * @param request  Agent层请求参数
     * @param callback Agent层流式回调
     * @return Agent层流式控制句柄
     */
    public abstract AgentStreamingHandle thinkStreaming(AgentLLMRequest request, AgentStreamingCallback callback);

    // ========== 异步流式调用 ==========

    /**
     * 异步流式调用Agent进行思考
     *
     * @param userInput 用户输入
     * @param callback  Agent层流式回调
     * @return Agent层流式控制句柄
     */
    public abstract AgentStreamingHandle thinkStreamAsync(String userInput, AgentStreamingCallback callback);

    /**
     * 异步流式调用Agent进行思考（带完整请求参数）
     *
     * @param request  Agent层请求参数
     * @param callback Agent层流式回调
     * @return Agent层流式控制句柄
     */
    public abstract AgentStreamingHandle thinkStreamAsync(AgentLLMRequest request, AgentStreamingCallback callback);
}
