package ai.sagesource.sagent.agent.prompt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 提示词渲染上下文
 *
 * @author: sage.xue
 * @time: 2026/3/30
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PromptRenderContext {

    /**
     * 渲染参数
     */
    private final Map<String, Object> parameters;

    /**
     * 获取参数值
     *
     * @param key 参数名
     * @return 参数值Optional
     */
    public Optional<Object> get(String key) {
        return Optional.ofNullable(parameters.get(key));
    }

    /**
     * 获取参数值，如果不存在返回默认值
     *
     * @param key          参数名
     * @param defaultValue 默认值
     * @return 参数值或默认值
     */
    public Object getOrDefault(String key, Object defaultValue) {
        return parameters.getOrDefault(key, defaultValue);
    }

    /**
     * 创建空上下文
     *
     * @return 空上下文
     */
    public static PromptRenderContext empty() {
        return new PromptRenderContext(Collections.emptyMap());
    }

    /**
     * 从Map创建上下文
     *
     * @param parameters 参数Map
     * @return 渲染上下文
     */
    public static PromptRenderContext of(Map<String, Object> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return empty();
        }
        return new PromptRenderContext(new HashMap<>(parameters));
    }

    /**
     * 创建单参数上下文
     *
     * @param key   参数名
     * @param value 参数值
     * @return 渲染上下文
     */
    public static PromptRenderContext of(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return new PromptRenderContext(map);
    }

    /**
     * 添加上下文参数（返回新实例）
     *
     * @param key   参数名
     * @param value 参数值
     * @return 新的渲染上下文
     */
    public PromptRenderContext with(String key, Object value) {
        Map<String, Object> newParams = new HashMap<>(parameters);
        newParams.put(key, value);
        return new PromptRenderContext(newParams);
    }
}
