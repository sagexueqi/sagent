package ai.sagesource.sagent.agent.prompt;

import ai.sagesource.sagent.agent.config.PromptConfig;
import ai.sagesource.sagent.agent.exception.SagentPromptException;
import ai.sagesource.sagent.agent.prompt.loader.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 提示词管理器
 * 提供统一的提示词模板加载和渲染入口
 *
 * @author: sage.xue
 * @time: 2026/3/30
 */
@Slf4j
@Getter
public class PromptManager {

    private final PromptLoader   loader;
    private final PromptRenderer renderer;

    /**
     * 使用默认配置创建管理器
     */
    public PromptManager() {
        this(PromptConfig.getDefault());
    }

    /**
     * 使用配置创建管理器
     *
     * @param config 配置
     */
    public PromptManager(PromptConfig config) {
        this.loader = buildLoader(config);
        this.renderer = SimplePromptRenderer.builder().build();
        log.info("PromptManager initialized with loader: {}", loader.getName());
    }

    /**
     * 使用自定义加载器和渲染器创建管理器
     *
     * @param loader   加载器
     * @param renderer 渲染器
     */
    public PromptManager(PromptLoader loader, PromptRenderer renderer) {
        this.loader = loader;
        this.renderer = renderer;
    }

    /**
     * 构建组合加载器
     */
    private PromptLoader buildLoader(PromptConfig config) {
        List<PromptLoader> loaders = new ArrayList<>();

        // 添加自定义加载器（优先级最高）
        if (config.getCustomLoaders() != null) {
            loaders.addAll(config.getCustomLoaders());
        }

        // 添加默认文件系统加载器
        if (config.isEnableDefaultFileSystemLoader()) {
            loaders.add(FileSystemPromptLoader.createDefault());
        }

        // 添加默认类路径加载器（优先级最低）
        if (config.isEnableDefaultClassPathLoader()) {
            loaders.add(ClassPathPromptLoader.builder().build());
        }

        return CompositePromptLoader.builder()
                .loaders(loaders)
                .build();
    }

    /**
     * 加载模板
     *
     * @param templateName 模板名称
     * @return 模板对象
     * @throws SagentPromptException.TemplateNotFoundException 如果模板不存在
     */
    public PromptTemplate load(String templateName) {
        return PromptTemplate.fromLoader(loader, templateName, renderer)
                .orElseThrow(() -> new SagentPromptException.TemplateNotFoundException(templateName));
    }

    /**
     * 尝试加载模板
     *
     * @param templateName 模板名称
     * @return 模板Optional
     */
    public Optional<PromptTemplate> tryLoad(String templateName) {
        return PromptTemplate.fromLoader(loader, templateName, renderer);
    }

    /**
     * 加载并渲染模板（便捷方法）
     *
     * @param templateName 模板名称
     * @param parameters   参数
     * @return 渲染后的提示词
     */
    public String render(String templateName, Map<String, Object> parameters) {
        return load(templateName).render(parameters);
    }

    /**
     * 加载并渲染模板（便捷方法）
     *
     * @param templateName 模板名称
     * @param context      渲染上下文
     * @return 渲染后的提示词
     */
    public String render(String templateName, PromptRenderContext context) {
        return load(templateName).render(context);
    }

    /**
     * 加载并渲染模板（无参数）
     *
     * @param templateName 模板名称
     * @return 渲染后的提示词
     */
    public String render(String templateName) {
        return load(templateName).render();
    }

    /**
     * 检查模板是否存在
     *
     * @param templateName 模板名称
     * @return 是否存在
     */
    public boolean exists(String templateName) {
        return loader.supports(templateName);
    }

    /**
     * 创建Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * PromptManager构建器
     */
    public static class Builder {
        private PromptConfig   config;
        private PromptLoader   loader;
        private PromptRenderer renderer = SimplePromptRenderer.builder().build();

        public Builder config(PromptConfig config) {
            this.config = config;
            this.loader = null;
            return this;
        }

        public Builder loader(PromptLoader loader) {
            this.loader = loader;
            this.config = null;
            return this;
        }

        public Builder renderer(PromptRenderer renderer) {
            this.renderer = renderer;
            return this;
        }

        public PromptManager build() {
            if (loader != null) {
                return new PromptManager(loader, renderer);
            }
            return new PromptManager(config != null ? config : PromptConfig.getDefault());
        }
    }
}
