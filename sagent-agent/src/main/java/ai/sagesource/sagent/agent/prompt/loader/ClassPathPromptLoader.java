package ai.sagesource.sagent.agent.prompt.loader;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 类路径提示词模板加载器
 *
 * @author: sage.xue
 * @time: 2026/3/30
 */
@Slf4j
@Getter
@Builder
public class ClassPathPromptLoader implements PromptLoader {

    /**
     * 基础路径（类路径下）
     */
    @Builder.Default
    private final String basePath = "prompts/";

    /**
     * 模板文件扩展名
     */
    @Builder.Default
    private final String extension = ".prompt";

    /**
     * 加载器名称
     */
    @Builder.Default
    private final String name = "ClassPathPromptLoader";

    /**
     * 优先级
     */
    @Builder.Default
    private final int priority = 10;

    /**
     * 类加载器
     */
    @Builder.Default
    private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    @Override
    public Optional<String> load(String templateName) {
        String fullPath = basePath + templateName + extension;
        InputStream inputStream = classLoader.getResourceAsStream(fullPath);

        if (inputStream == null) {
            log.debug("Template not found in classpath: {}", fullPath);
            return Optional.empty();
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String content = reader.lines().collect(Collectors.joining("\n"));
            log.debug("Loaded template from classpath: {}", fullPath);
            return Optional.of(content);
        } catch (Exception e) {
            log.warn("Failed to read template from classpath: {}", fullPath, e);
            return Optional.empty();
        }
    }

    @Override
    public boolean supports(String templateName) {
        String fullPath = basePath + templateName + extension;
        return classLoader.getResource(fullPath) != null;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public String getName() {
        return name;
    }
}
