package ai.sagesource.sagent.agent.prompt;

import ai.sagesource.sagent.agent.prompt.loader.PromptLoader;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

/**
 * 提示词模板
 * 封装模板内容和渲染能力
 *
 * @author: sage.xue
 * @time: 2026/3/30
 */
@Slf4j
@Getter
@Builder
public class PromptTemplate {

    /**
     * 模板名称
     */
    private final String name;

    /**
     * 模板原始内容
     */
    private final String content;

    /**
     * 模板渲染器
     */
    @Builder.Default
    private final PromptRenderer renderer = SimplePromptRenderer.builder().build();

    /**
     * 渲染模板
     *
     * @param context 渲染上下文
     * @return 渲染后的提示词
     */
    public String render(PromptRenderContext context) {
        return renderer.render(content, context);
    }

    /**
     * 渲染模板（便捷方法）
     *
     * @param parameters 参数Map
     * @return 渲染后的提示词
     */
    public String render(Map<String, Object> parameters) {
        return render(PromptRenderContext.of(parameters));
    }

    /**
     * 渲染模板（无参数）
     *
     * @return 渲染后的提示词
     */
    public String render() {
        return render(PromptRenderContext.empty());
    }

    /**
     * 从加载器加载模板
     *
     * @param loader       加载器
     * @param templateName 模板名称
     * @return 模板Optional
     */
    public static Optional<PromptTemplate> fromLoader(PromptLoader loader, String templateName) {
        return loader.load(templateName)
                .map(content -> PromptTemplate.builder()
                        .name(templateName)
                        .content(content)
                        .build());
    }

    /**
     * 从加载器加载模板（指定渲染器）
     *
     * @param loader       加载器
     * @param templateName 模板名称
     * @param renderer     渲染器
     * @return 模板Optional
     */
    public static Optional<PromptTemplate> fromLoader(PromptLoader loader, String templateName, PromptRenderer renderer) {
        return loader.load(templateName)
                .map(content -> PromptTemplate.builder()
                        .name(templateName)
                        .content(content)
                        .renderer(renderer)
                        .build());
    }
}
