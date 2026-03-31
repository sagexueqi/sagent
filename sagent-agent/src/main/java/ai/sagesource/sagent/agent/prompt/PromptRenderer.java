package ai.sagesource.sagent.agent.prompt;

import java.util.Map;

/**
 * 提示词模板渲染器接口
 *
 * @author: sage.xue
 * @time: 2026/3/30
 */
public interface PromptRenderer {

    /**
     * 渲染模板，替换占位符
     *
     * @param template   模板内容
     * @param context    渲染上下文
     * @return 渲染后的内容
     */
    String render(String template, PromptRenderContext context);

    /**
     * 渲染模板（便捷方法）
     *
     * @param template   模板内容
     * @param parameters 参数映射
     * @return 渲染后的内容
     */
    default String render(String template, Map<String, Object> parameters) {
        return render(template, PromptRenderContext.of(parameters));
    }

    /**
     * 渲染模板（无参数）
     *
     * @param template 模板内容
     * @return 渲染后的内容（原样返回）
     */
    default String render(String template) {
        return render(template, PromptRenderContext.empty());
    }
}
