package ai.sagesource.sagent.llm.client;

/**
 * LLM Client Abstract Interface
 *
 * @author: sage.xue
 * @time: 2026/3/14
 */
public interface LLMClient<CLIENT> {

    /**
     * define client name
     *
     * @return
     */
    String name();

    /**
     * obtain actual llm client
     *
     * @return
     */
    CLIENT client();

    /**
     * return model name
     *
     * @return
     */
    String model();

    /**
     * max completion token
     *
     * @return
     */
    long maxToken();
}
