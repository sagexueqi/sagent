package ai.sagesource.sagent.agent.prompt.loader;

import lombok.Builder;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 组合提示词模板加载器
 * 按优先级顺序尝试多个加载器
 *
 * @author: sage.xue
 * @time: 2026/3/30
 */
@Slf4j
@Builder
public class CompositePromptLoader implements PromptLoader {

    /**
     * 加载器列表
     */
    @Singular
    private final List<PromptLoader> loaders;

    /**
     * 加载器名称
     */
    @Builder.Default
    private final String name = "CompositePromptLoader";

    @Override
    public Optional<String> load(String templateName) {
        // 按优先级降序排序
        List<PromptLoader> sortedLoaders = loaders.stream()
                .sorted(Comparator.comparingInt(PromptLoader::getPriority).reversed())
                .toList();

        for (PromptLoader loader : sortedLoaders) {
            if (loader.supports(templateName)) {
                Optional<String> content = loader.load(templateName);
                if (content.isPresent()) {
                    log.debug("Template '{}' loaded by {}", templateName, loader.getName());
                    return content;
                }
            }
        }

        log.warn("Template '{}' not found in any loader", templateName);
        return Optional.empty();
    }

    @Override
    public boolean supports(String templateName) {
        return loaders.stream().anyMatch(loader -> loader.supports(templateName));
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * 添加加载器
     *
     * @param loader 加载器
     * @return 新的组合加载器
     */
    public CompositePromptLoader addLoader(PromptLoader loader) {
        List<PromptLoader> newLoaders = new java.util.ArrayList<>(loaders);
        newLoaders.add(loader);
        return CompositePromptLoader.builder()
                .loaders(newLoaders)
                .name(name)
                .build();
    }
}
