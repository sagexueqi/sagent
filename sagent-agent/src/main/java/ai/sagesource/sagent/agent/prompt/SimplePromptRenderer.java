package ai.sagesource.sagent.agent.prompt;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;

import java.util.Map;

/**
 * 简单提示词模板渲染器
 * 基于Apache Commons Text的StringSubstitutor实现高性能占位符替换
 *
 * 支持占位符格式（可配置）:
 * - {{placeholder}} 或 {{placeholder:defaultValue}}
 * - ${placeholder} 或 ${placeholder:defaultValue}
 *
 * @author: sage.xue
 * @time: 2026/3/30
 */
@Slf4j
@Builder
public class SimplePromptRenderer implements PromptRenderer {

    /**
     * 前缀字符串
     */
    @Builder.Default
    private final String prefix = "{{";

    /**
     * 后缀字符串
     */
    @Builder.Default
    private final String suffix = "}}";

    /**
     * 是否保留未匹配的占位符
     */
    @Builder.Default
    private final boolean preserveEscapes = false;

    @Override
    public String render(String template, PromptRenderContext context) {
        if (template == null || template.isEmpty()) {
            return template;
        }

        if (context == null) {
            context = PromptRenderContext.empty();
        }

        // 使用Apache Commons Text的StringSubstitutor
        StringLookup lookup = getStringLookup(context);

        StringSubstitutor substitutor = new StringSubstitutor(
                lookup,
                prefix,
                suffix,
                ':'
        );
        substitutor.setPreserveEscapes(preserveEscapes);

        return substitutor.replace(template);
    }

    private static StringLookup getStringLookup(PromptRenderContext context) {
        return key -> {
            if (key == null) {
                return null;
            }

            // 检查是否有默认值（格式: key:defaultValue）
            String actualKey = key;
            String defaultValue = null;

            int delimiterIndex = key.indexOf(':');
            if (delimiterIndex > 0) {
                actualKey = key.substring(0, delimiterIndex).trim();
                defaultValue = key.substring(delimiterIndex + 1).trim();
            }

            // 从上下文获取值，如果不存在则使用默认值
            Object value = context.getOrDefault(actualKey, defaultValue);
            return value != null ? value.toString() : null;
        };
    }

    /**
     * 创建使用${}格式的渲染器
     *
     * @return 渲染器实例
     */
    public static SimplePromptRenderer createDollarStyle() {
        return SimplePromptRenderer.builder()
                .prefix("${")
                .suffix("}")
                .build();
    }

    /**
     * 创建使用{{}}格式的渲染器（默认）
     *
     * @return 渲染器实例
     */
    public static SimplePromptRenderer createMustacheStyle() {
        return SimplePromptRenderer.builder()
                .prefix("{{")
                .suffix("}}")
                .build();
    }
}
