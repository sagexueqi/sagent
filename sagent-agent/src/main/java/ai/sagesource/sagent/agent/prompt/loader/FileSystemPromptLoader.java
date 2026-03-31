package ai.sagesource.sagent.agent.prompt.loader;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * 文件系统提示词模板加载器
 *
 * @author: sage.xue
 * @time: 2026/3/30
 */
@Slf4j
@Getter
@Builder
public class FileSystemPromptLoader implements PromptLoader {

    /**
     * 基础路径
     */
    private final Path basePath;

    /**
     * 模板文件扩展名
     */
    @Builder.Default
    private final String extension = ".prompt";

    /**
     * 加载器名称
     */
    @Builder.Default
    private final String name = "FileSystemPromptLoader";

    /**
     * 优先级
     */
    @Builder.Default
    private final int priority = 100;

    @Override
    public Optional<String> load(String templateName) {
        Path templatePath = basePath.resolve(templateName + extension);
        if (!Files.exists(templatePath)) {
            log.debug("Template file not found: {}", templatePath);
            return Optional.empty();
        }

        try {
            String content = Files.readString(templatePath);
            log.debug("Loaded template from file: {}", templatePath);
            return Optional.of(content);
        } catch (IOException e) {
            log.warn("Failed to read template file: {}", templatePath, e);
            return Optional.empty();
        }
    }

    @Override
    public boolean supports(String templateName) {
        Path templatePath = basePath.resolve(templateName + extension);
        return Files.exists(templatePath) && Files.isReadable(templatePath);
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * 创建默认的文件系统加载器（从项目根目录的prompts文件夹加载）
     *
     * @return FileSystemPromptLoader实例
     */
    public static FileSystemPromptLoader createDefault() {
        Path defaultPath = Paths.get(".").toAbsolutePath().normalize().resolve("prompts");
        return FileSystemPromptLoader.builder()
                .basePath(defaultPath)
                .name("DefaultFileSystemPromptLoader")
                .priority(50)
                .build();
    }

    /**
     * 从指定路径创建加载器
     *
     * @param path 路径字符串
     * @return FileSystemPromptLoader实例
     */
    public static FileSystemPromptLoader fromPath(String path) {
        return FileSystemPromptLoader.builder()
                .basePath(Paths.get(path).toAbsolutePath().normalize())
                .build();
    }
}
