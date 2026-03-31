package ai.sagesource.sagent.agent.prompt.loader;

import java.util.Optional;

/**
 * 提示词模板加载器接口
 *
 * @author: sage.xue
 * @time: 2026/3/30
 */
public interface PromptLoader {

    /**
     * 根据模板名称加载模板内容
     *
     * @param templateName 模板名称（不含扩展名）
     * @return 模板内容，如果不存在则返回Optional.empty()
     */
    Optional<String> load(String templateName);

    /**
     * 检查是否支持加载指定模板
     *
     * @param templateName 模板名称
     * @return 是否支持
     */
    boolean supports(String templateName);

    /**
     * 获取加载器名称
     *
     * @return 加载器名称
     */
    String getName();

    /**
     * 获取加载器优先级，数值越大优先级越高
     *
     * @return 优先级
     */
    default int getPriority() {
        return 0;
    }
}
