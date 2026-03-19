package ai.sagesource.sagent.llm.client;

import lombok.Data;

/**
 * LLM配置
 *
 * @author: sage.xue
 * @time: 2026/3/14
 */
@Data
public class LLMClientConfig {
    /**
     * BASE API URL
     */
    private String baseUrl;
    /**
     * API KEY
     */
    private String apiKey;
    /**
     * Model Name
     */
    private String model;
    /**
     * Max token Number
     */
    private Long   maxToken;
    /**
     * Connection Timeout(s)
     */
    private int    connectionTimeout;
    /**
     * Read Timeout(s)
     */
    private int    readTimeout;
}
