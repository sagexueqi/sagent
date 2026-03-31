package ai.sagesource.sagent.agent.config;

import ai.sagesource.sagent.agent.prompt.loader.ClassPathPromptLoader;
import ai.sagesource.sagent.agent.prompt.loader.FileSystemPromptLoader;
import ai.sagesource.sagent.agent.prompt.loader.PromptLoader;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

/**
 * 提示词组件配置
 *
 * @author: sage.xue
 * @time: 2026/3/30
 */
@Getter
@Builder
public class PromptConfig {

    /**
     * 是否启用默认文件系统加载器（从./prompts加载）
     */
    @Builder.Default
    private final boolean enableDefaultFileSystemLoader = true;

    /**
     * 是否启用默认类路径加载器（从classpath:/prompts加载）
     */
    @Builder.Default
    private final boolean enableDefaultClassPathLoader = true;

    /**
     * 自定义加载器列表
     */
    @Singular
    private final List<PromptLoader> customLoaders;

    /**
     * 模板文件扩展名
     */
    @Builder.Default
    private final String templateExtension = ".prompt";

    /**
     * 获取默认配置
     *
     * @return 默认配置
     */
    public static PromptConfig getDefault() {
        return PromptConfig.builder().build();
    }

    /**
     * 创建只使用文件系统的配置
     *
     * @param path 模板路径
     * @return 配置对象
     */
    public static PromptConfig fileSystemOnly(String path) {
        return PromptConfig.builder()
                .enableDefaultFileSystemLoader(false)
                .enableDefaultClassPathLoader(false)
                .customLoader(FileSystemPromptLoader.fromPath(path))
                .build();
    }

    /**
     * 创建只使用类路径的配置
     *
     * @return 配置对象
     */
    public static PromptConfig classPathOnly() {
        return PromptConfig.builder()
                .enableDefaultFileSystemLoader(false)
                .enableDefaultClassPathLoader(false)
                .customLoader(ClassPathPromptLoader.builder().build())
                .build();
    }
}
