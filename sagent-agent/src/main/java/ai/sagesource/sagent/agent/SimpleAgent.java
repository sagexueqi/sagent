package ai.sagesource.sagent.agent;

import ai.sagesource.sagent.agent.config.AgentConfig;
import ai.sagesource.sagent.agent.context.AgentContext;
import ai.sagesource.sagent.agent.llm.AgentLLMRequest;
import ai.sagesource.sagent.agent.llm.AgentLLMResponse;
import ai.sagesource.sagent.agent.llm.AgentStreamingCallback;
import ai.sagesource.sagent.agent.llm.AgentStreamingHandle;
import ai.sagesource.sagent.llm.completion.LLMCompletionStreamingCallback;
import ai.sagesource.sagent.llm.completion.chat.models.messages.ChatLLMCompletionAssistantMessage;
import ai.sagesource.sagent.llm.completion.chat.models.messages.ChatLLMCompletionMessage;
import ai.sagesource.sagent.llm.completion.chat.models.messages.ChatLLMCompletionSystemMessage;
import ai.sagesource.sagent.llm.completion.chat.models.messages.ChatLLMCompletionUserMessage;
import ai.sagesource.sagent.llm.completion.chat.models.response.ChatLLMCompletionResponse;
import ai.sagesource.sagent.llm.function.FunctionToolDefinition;
import ai.sagesource.sagent.agent.exception.SagentAgentException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 简单Agent实现
 * 支持基础的对话功能，无工具调用能力
 * 构建消息时System Prompt始终在最前面，保存历史时忽略System Prompt
 *
 * @author: sage.xue
 * @time: 2026/4/6
 */
@Slf4j
public class SimpleAgent extends AbstractAgent {

    public SimpleAgent(AgentConfig config) {
        super(config);
    }

    public SimpleAgent(String name) {
        super(name);
    }

    @Override
    protected void doInitialize() {
        // SimpleAgent无需额外的初始化逻辑
        log.debug("SimpleAgent [{}] initialized", name());
    }

    // ========== 同步调用 ==========

    @Override
    public AgentLLMResponse think(String userInput) {
        return think(AgentLLMRequest.builder()
                .userInput(userInput)
                .build());
    }

    @Override
    public AgentLLMResponse think(AgentLLMRequest request) {
        ensureInitialized();
        String contextId = extractContextId(request);
        AgentContext context = buildContext(contextId);

        try {
            // 构建消息列表（System Prompt在最前面）
            List<ChatLLMCompletionMessage> messages = buildMessages(request.userInput(), context);

            // 调用LLM
            float temperature = request.temperature() != null ? request.temperature() : config.getTemperature();
            ChatLLMCompletionResponse llmResponse = llmCompletion.thinking(
                    messages,
                    Collections.emptyList(),
                    temperature
            );

            // 保存历史消息（忽略System Prompt）
            saveHistoryMessages(context, request.userInput(), llmResponse.message());
            saveContext(context);

            // 构建响应
            return AgentLLMResponse.builder()
                    .content(llmResponse.message().content())
                    .build();
        } catch (Exception e) {
            log.error("SimpleAgent [{}] think failed", name(), e);
            throw new SagentAgentException.ExecutionException(name(), e.getMessage(), e);
        }
    }

    // ========== 异步调用 ==========

    @Override
    public CompletableFuture<AgentLLMResponse> thinkAsync(String userInput) {
        return CompletableFuture.supplyAsync(() -> think(userInput), getExecutor());
    }

    @Override
    public CompletableFuture<AgentLLMResponse> thinkAsync(AgentLLMRequest request) {
        return CompletableFuture.supplyAsync(() -> think(request), getExecutor());
    }

    // ========== 流式调用 ==========

    @Override
    public AgentStreamingHandle thinkStreaming(String userInput, AgentStreamingCallback callback) {
        return thinkStreaming(AgentLLMRequest.builder()
                .userInput(userInput)
                .build(), callback);
    }

    @Override
    public AgentStreamingHandle thinkStreaming(AgentLLMRequest request, AgentStreamingCallback callback) {
        ensureInitialized();
        String contextId = extractContextId(request);
        AgentContext context = buildContext(contextId);

        // 构建消息列表（System Prompt在最前面）
        List<ChatLLMCompletionMessage> messages = buildMessages(request.userInput(), context);

        // 用于收集完整响应
        StringBuilder contentBuilder = new StringBuilder();

        // 包装回调
        LLMCompletionStreamingCallback<ChatLLMCompletionResponse> wrappedCallback =
                new LLMCompletionStreamingCallback<>() {
                    @Override
                    public boolean onToken(ChatLLMCompletionResponse response) {
                        if (response.message() != null && response.message().content() != null) {
                            contentBuilder.append(response.message().content());
                        }
                        return callback.onToken(AgentLLMResponse.builder()
                                .content(response.message() != null ? response.message().content() : null)
                                .build());
                    }

                    @Override
                    public void onCompletion(ChatLLMCompletionResponse response) {
                        // 保存历史消息（忽略System Prompt）
                        saveHistoryMessages(context, request.userInput(), response.message());
                        saveContext(context);

                        callback.onCompletion(AgentLLMResponse.builder()
                                .content(response.message() != null ? response.message().content() : null)
                                .build());
                    }

                    @Override
                    public void onCancelled() {
                        // 即使取消也尝试保存已生成的内容
                        if (contentBuilder.length() > 0) {
                            ChatLLMCompletionAssistantMessage assistantMessage = 
                                    new ChatLLMCompletionAssistantMessage();
                            assistantMessage.content(contentBuilder.toString());
                            saveHistoryMessages(context, request.userInput(), assistantMessage);
                            saveContext(context);
                        }
                        callback.onCancelled();
                    }

                    @Override
                    public void onError(Throwable t) {
                        callback.onError(t);
                    }
                };

        float temperature = request.temperature() != null ? request.temperature() : config.getTemperature();
        return new AgentStreamingHandle(
                llmCompletion.thinking_streaming(
                        messages,
                        Collections.emptyList(),
                        temperature,
                        wrappedCallback
                )
        );
    }

    // ========== 异步流式调用 ==========

    @Override
    public AgentStreamingHandle thinkStreamAsync(String userInput, AgentStreamingCallback callback) {
        return thinkStreamAsync(AgentLLMRequest.builder()
                .userInput(userInput)
                .build(), callback);
    }

    @Override
    public AgentStreamingHandle thinkStreamAsync(AgentLLMRequest request, AgentStreamingCallback callback) {
        ensureInitialized();
        String contextId = extractContextId(request);
        AgentContext context = buildContext(contextId);

        // 构建消息列表（System Prompt在最前面）
        List<ChatLLMCompletionMessage> messages = buildMessages(request.userInput(), context);

        // 用于收集完整响应
        StringBuilder contentBuilder = new StringBuilder();

        // 包装回调
        LLMCompletionStreamingCallback<ChatLLMCompletionResponse> wrappedCallback =
                new LLMCompletionStreamingCallback<>() {
                    @Override
                    public boolean onToken(ChatLLMCompletionResponse response) {
                        if (response.message() != null && response.message().content() != null) {
                            contentBuilder.append(response.message().content());
                        }
                        return callback.onToken(AgentLLMResponse.builder()
                                .content(response.message() != null ? response.message().content() : null)
                                .build());
                    }

                    @Override
                    public void onCompletion(ChatLLMCompletionResponse response) {
                        // 保存历史消息（忽略System Prompt）
                        saveHistoryMessages(context, request.userInput(), response.message());
                        saveContext(context);

                        callback.onCompletion(AgentLLMResponse.builder()
                                .content(response.message() != null ? response.message().content() : null)
                                .build());
                    }

                    @Override
                    public void onCancelled() {
                        // 即使取消也尝试保存已生成的内容
                        if (contentBuilder.length() > 0) {
                            ChatLLMCompletionAssistantMessage assistantMessage = 
                                    new ChatLLMCompletionAssistantMessage();
                            assistantMessage.content(contentBuilder.toString());
                            saveHistoryMessages(context, request.userInput(), assistantMessage);
                            saveContext(context);
                        }
                        callback.onCancelled();
                    }

                    @Override
                    public void onError(Throwable t) {
                        callback.onError(t);
                    }
                };

        float temperature = request.temperature() != null ? request.temperature() : config.getTemperature();
        return new AgentStreamingHandle(
                llmCompletion.thinking_stream_async(
                        messages,
                        Collections.emptyList(),
                        temperature,
                        wrappedCallback,
                        getExecutor()
                )
        );
    }

    /**
     * 获取线程池执行器
     * 优先使用AgentConfig中配置的Executor，否则使用默认异步执行器
     *
     * @return Executor实例
     */
    private Executor getExecutor() {
        // 可以扩展AgentConfig支持配置自定义Executor
        // 目前使用CompletableFuture默认的异步执行器
        return CompletableFuture.delayedExecutor(0, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    // ========== 私有方法 ==========

    /**
     * 构建发送给LLM的消息列表
     * System Prompt始终放在最前面，然后是历史消息，最后是当前用户输入
     *
     * @param userInput 用户输入
     * @param context   上下文
     * @return 消息列表
     */
    private List<ChatLLMCompletionMessage> buildMessages(String userInput, AgentContext context) {
        List<ChatLLMCompletionMessage> messages = new ArrayList<>();

        // 1. System Prompt始终在最前面
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            ChatLLMCompletionSystemMessage systemMessage = new ChatLLMCompletionSystemMessage();
            systemMessage.content(systemPrompt);
            messages.add(systemMessage);
        }

        // 2. 添加历史消息
        if (context.historyMessages() != null && !context.historyMessages().isEmpty()) {
            messages.addAll(context.historyMessages());
        }

        // 3. 添加当前用户输入
        ChatLLMCompletionUserMessage userMessage = new ChatLLMCompletionUserMessage();
        userMessage.content(userInput);
        messages.add(userMessage);

        return messages;
    }

    /**
     * 保存历史消息到Context
     * 忽略System Prompt，只保存用户输入和助手回复
     *
     * @param context          上下文
     * @param userInput        用户输入
     * @param assistantMessage 助手回复
     */
    private void saveHistoryMessages(AgentContext context, String userInput, 
                                     ChatLLMCompletionAssistantMessage assistantMessage) {
        // 保存用户消息
        ChatLLMCompletionUserMessage userMessage = new ChatLLMCompletionUserMessage();
        userMessage.content(userInput);
        context.addMessage(userMessage);

        // 保存助手消息
        if (assistantMessage != null) {
            context.addMessage(assistantMessage);
        }

        log.debug("SimpleAgent [{}] saved history messages, current size: {}", 
                name(), context.historyMessages().size());
    }

    /**
     * 确保Agent已初始化
     */
    private void ensureInitialized() {
        if (!isInitialized()) {
            throw new SagentAgentException.NotInitializedException(name());
        }
    }
}
