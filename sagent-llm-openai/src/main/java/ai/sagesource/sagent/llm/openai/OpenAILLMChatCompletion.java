package ai.sagesource.sagent.llm.openai;

import ai.sagesource.sagent.base.exception.SagentLLMException;
import ai.sagesource.sagent.llm.completion.LLMCompletionStreamingCallback;
import ai.sagesource.sagent.llm.completion.LLMCompletionStreamingHandle;
import ai.sagesource.sagent.llm.completion.chat.ChatLLMCompletion;
import ai.sagesource.sagent.llm.completion.chat.models.messages.*;
import ai.sagesource.sagent.llm.completion.chat.models.response.ChatLLMCompletionResponse;
import ai.sagesource.sagent.llm.completion.chat.models.response.ChatLLMCompletionResponseToolCall;
import ai.sagesource.sagent.llm.exceptions.SagentLLMConnectionException;
import ai.sagesource.sagent.llm.exceptions.SagentLLMServiceException;
import ai.sagesource.sagent.llm.function.FunctionToolDefinition;
import ai.sagesource.sagent.llm.openai.support.OpenAIChatCompletionMessageToolCallSupport;
import ai.sagesource.sagent.llm.openai.support.OpenAIFunctionDefinitionSupport;
import com.openai.client.OpenAIClient;
import com.openai.core.http.StreamResponse;
import com.openai.errors.OpenAIServiceException;
import com.openai.errors.RateLimitException;
import com.openai.errors.UnauthorizedException;
import com.openai.helpers.ChatCompletionAccumulator;
import com.openai.models.chat.completions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 实现基于OPENAI SDK的Chat Completion
 *
 * @author: sage.xue
 * @time: 2026/3/16
 */
public class OpenAILLMChatCompletion extends ChatLLMCompletion<OpenAILLMClient> {

    public OpenAILLMChatCompletion(OpenAILLMClient llmClient) {
        super(llmClient);
    }

    @Override
    public ChatLLMCompletionResponse thinking(List<ChatLLMCompletionMessage> messages,
                                              List<FunctionToolDefinition> functions,
                                              float temperature) {
        try {
            ChatLLMCompletionResponse         response         = new ChatLLMCompletionResponse();
            ChatLLMCompletionAssistantMessage assistantMessage = new ChatLLMCompletionAssistantMessage();
            response.message(assistantMessage);

            // 获取OPENAI CLIENT
            OpenAIClient openAIClient = llmClient.client();
            // 构建参数
            ChatCompletionCreateParams params = this.chatCompletionCreateParams(messages, functions, temperature);
            // 调用LLM
            ChatCompletion chatCompletion = openAIClient.chat().completions().create(params);
            // 构建响应
            return toolCallsResponseBuild(chatCompletion);
        } catch (Exception e) {
            throw translateException(e);
        }
    }

    @Override
    public LLMCompletionStreamingHandle thinking_streaming(List<ChatLLMCompletionMessage> messages,
                                                           List<FunctionToolDefinition> functions,
                                                           float temperature,
                                                           LLMCompletionStreamingCallback<ChatLLMCompletionResponse> streamingCallback) {
        return coreThinkingStreaming(messages, functions, temperature, streamingCallback, new LLMCompletionStreamingHandle());
    }

    @Override
    public LLMCompletionStreamingHandle thinking_stream_async(List<ChatLLMCompletionMessage> messages,
                                                              List<FunctionToolDefinition> functions,
                                                              float temperature,
                                                              LLMCompletionStreamingCallback<ChatLLMCompletionResponse> streamingCallback,
                                                              Executor executor) {
        LLMCompletionStreamingHandle handle = new LLMCompletionStreamingHandle();
        executor.execute(() -> {
            // 复用同步核心实现，handler在压入线程池前传入
            coreThinkingStreaming(messages, functions, temperature, streamingCallback, handle);
        });
        return handle;
    }

    @Override
    protected SagentLLMException translateException(Exception e) {
        // OpenAI SDK 的异常体系 -> 框架异常
        if (e instanceof RateLimitException oae) {
            return new SagentLLMServiceException(
                    429, "rate_limit", "OpenAI rate limit exceeded", oae);
        }
        if (e instanceof UnauthorizedException oae) {
            return new SagentLLMServiceException(
                    401, "auth_error", "OpenAI authentication failed", oae);
        }
        // https://github.com/openai/openai-java/tree/main?tab=readme-ov-file#error-handling
        if (e instanceof OpenAIServiceException oae) {
            return new SagentLLMServiceException(
                    oae.statusCode(), oae.code().orElse("unknown"), oae.getMessage(), oae);
        }
        if (e instanceof IOException) {
            return new SagentLLMConnectionException(
                    "OpenAI connection error: " + e.getMessage(), e);
        }
        if (e instanceof SagentLLMException oae) {
            return oae;
        }
        return new SagentLLMException("unexpected exception", e);
    }

    /**
     * 流式输出核心逻辑
     *
     * @param messages
     * @param functions
     * @param temperature
     * @param streamingCallback
     * @return
     */
    private LLMCompletionStreamingHandle coreThinkingStreaming(List<ChatLLMCompletionMessage> messages,
                                                               List<FunctionToolDefinition> functions,
                                                               float temperature,
                                                               LLMCompletionStreamingCallback<ChatLLMCompletionResponse> streamingCallback,
                                                               LLMCompletionStreamingHandle handle) {
        // 获取OPENAI CLIENT
        OpenAIClient openAIClient = llmClient.client();
        // 构建参数
        ChatCompletionCreateParams params = this.chatCompletionCreateParams(messages, functions, temperature);
        // 创建OPENAI累计器，处理流水响应的tool-calls
        ChatCompletionAccumulator accumulator = ChatCompletionAccumulator.create();

        // 流式请求
        try (StreamResponse<ChatCompletionChunk> streamResponse =
                     openAIClient.chat().completions().createStreaming(params)) {
            // 把 OPENAI 的流资源通过 lambda 注入
            handle.bindCloser(streamResponse::close);

            Stream<ChatCompletionChunk> stream = streamResponse.stream();
            stream
                    // 累加chunk
                    .peek(accumulator::accumulate)
                    .flatMap(completion -> completion.choices().stream())
                    .flatMap(choice -> choice.delta().content().stream())
                    .forEach(content -> {
                        if (handle.isCancelled()) return;

                        ChatLLMCompletionResponse         response         = new ChatLLMCompletionResponse();
                        ChatLLMCompletionAssistantMessage assistantMessage = new ChatLLMCompletionAssistantMessage();
                        response.message(assistantMessage);
                        assistantMessage.content(content);

                        // callback 返回 false 也触发取消
                        if (!streamingCallback.onToken(response)) {
                            handle.cancel();
                        }
                    });

            // 区分完成原因
            if (handle.isCancelled()) {
                streamingCallback.onCancelled();
            } else {
                ChatCompletion chatCompletion = accumulator.chatCompletion();
                streamingCallback.onCompletion(toolCallsResponseBuild(chatCompletion));
            }
        } catch (Exception e) {
            // 统一异常处理
            this.handleStreamingException(e, handle, streamingCallback);
        } finally {
            handle.markComplete();
        }
        return handle;
    }

    // 初始化chat completion create params
    private ChatCompletionCreateParams chatCompletionCreateParams(List<ChatLLMCompletionMessage> messages,
                                                                  List<FunctionToolDefinition> functions,
                                                                  float temperature) {
        ChatCompletionCreateParams.Builder builder = ChatCompletionCreateParams.builder();
        builder.model(this.llmClient.model())
                .temperature(temperature)
                .maxCompletionTokens(this.llmClient.maxToken());

        // 添加ToolDefinition
        if (functions != null && !functions.isEmpty()) {
            functions.forEach(function -> {
                // 每一个ToolDefinition
                builder.addTool(
                        ChatCompletionFunctionTool.builder()
                                .function(OpenAIFunctionDefinitionSupport.from(function))
                                .build()
                );
            });
        }

        // 添加Message
        messages.forEach(message -> {
            if (message instanceof ChatLLMCompletionAssistantMessage assistantMessage) {
                builder.addAssistantMessage(message.content());
                ChatCompletionAssistantMessageParam.Builder messageParamBuilder = ChatCompletionAssistantMessageParam.builder();

                // 支持tool-calls
                if (assistantMessage.toolCalls() != null && !assistantMessage.toolCalls().isEmpty()) {
                    messageParamBuilder.toolCalls(
                            assistantMessage.toolCalls().stream()
                                    .map(OpenAIChatCompletionMessageToolCallSupport::from)
                                    .collect(Collectors.toList())
                    );
                }

                builder.addMessage(messageParamBuilder
                        .content(message.content())
                        .build()
                );
            } else if (message instanceof ChatLLMCompletionDeveloperMessage) {
                builder.addDeveloperMessage(message.content());
            } else if (message instanceof ChatLLMCompletionSystemMessage) {
                builder.addSystemMessage(message.content());
            } else if (message instanceof ChatLLMCompletionUserMessage) {
                builder.addUserMessage(message.content());
            }

        });
        return builder.build();
    }

    // 构建tool-calls响应
    private ChatLLMCompletionResponse toolCallsResponseBuild(ChatCompletion chatCompletion) {
        ChatLLMCompletionResponse         response         = new ChatLLMCompletionResponse();
        ChatLLMCompletionAssistantMessage assistantMessage = new ChatLLMCompletionAssistantMessage();
        response.message(assistantMessage);
        List<ChatLLMCompletionResponseToolCall> toolCalls = new ArrayList<>();
        boolean hasToolCallFinish = chatCompletion.choices().stream()
                .anyMatch(choice -> "tool_calls".equalsIgnoreCase(choice.finishReason().asString()));

        chatCompletion.choices().stream()
                .map(ChatCompletion.Choice::message)
                .flatMap(message -> {
                    message.content().ifPresent(assistantMessage::content);
                    return message.toolCalls().stream().flatMap(Collection::stream);
                }).forEach(chatCompletionMessageToolCall -> {
                    toolCalls.add(
                            OpenAIChatCompletionMessageToolCallSupport.to(chatCompletionMessageToolCall)
                    );
                });
        assistantMessage.toolCalls(toolCalls);
        response.toolCalls(hasToolCallFinish);

        return response;
    }

}
