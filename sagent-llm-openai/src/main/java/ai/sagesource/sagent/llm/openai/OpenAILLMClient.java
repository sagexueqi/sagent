package ai.sagesource.sagent.llm.openai;

import ai.sagesource.sagent.llm.client.LLMClient;
import ai.sagesource.sagent.llm.client.LLMClientConfig;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.Timeout;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * Support OpenAI API LLM Client
 *
 * @author: sage.xue
 * @time: 2026/3/14
 */
@Slf4j
public class OpenAILLMClient implements LLMClient<OpenAIClient> {
    // OpenAI Client
    private       OpenAIClient    client;
    // Client Config
    private final LLMClientConfig clientConfig;

    public OpenAILLMClient(LLMClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    @Override
    public String name() {
        return "sagent_openai_llm_client";
    }

    @Override
    public OpenAIClient client() {
        return this.client;
    }

    @Override
    public String model() {
        return clientConfig.getModel();
    }

    @Override
    public long maxToken() {
        return clientConfig.getMaxToken();
    }

    /**
     * init client
     *
     * @return
     */
    public OpenAILLMClient init() {
        Timeout timeout = Timeout.builder()
                .connect(Duration.ofSeconds(this.clientConfig.getConnectionTimeout()))
                .read(Duration.ofSeconds(this.clientConfig.getReadTimeout()))
                .build();

        this.client = OpenAIOkHttpClient.builder()
                .baseUrl(this.clientConfig.getBaseUrl())
                .apiKey(this.clientConfig.getApiKey())
                .timeout(timeout)
                .build();

        log.info("> sagent-llm | init OpenAILLMClient Success. <");
        return this;
    }
}
