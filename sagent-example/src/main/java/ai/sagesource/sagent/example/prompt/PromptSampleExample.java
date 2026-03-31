package ai.sagesource.sagent.example.prompt;

import ai.sagesource.sagent.agent.PromptManager;
import ai.sagesource.sagent.agent.config.PromptConfig;
import ai.sagesource.sagent.agent.exception.SagentPromptException;
import ai.sagesource.sagent.agent.prompt.PromptRenderContext;
import ai.sagesource.sagent.agent.prompt.PromptRenderer;
import ai.sagesource.sagent.agent.prompt.PromptTemplate;
import ai.sagesource.sagent.agent.prompt.SimplePromptRenderer;
import ai.sagesource.sagent.agent.prompt.loader.ClassPathPromptLoader;
import ai.sagesource.sagent.agent.prompt.loader.CompositePromptLoader;
import ai.sagesource.sagent.agent.prompt.loader.FileSystemPromptLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * 提示词组件示例
 *
 * @author: sage.xue
 * @time: 2026/3/30
 */
public class PromptSampleExample {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    Sagent 提示词组件功能演示");
        System.out.println("========================================\n");

        // 1. 测试渲染器
        System.out.println("=== 测试 1: 模板渲染器 ===");
        testPromptRenderer();

        // 2. 测试文件系统加载器
        System.out.println("\n=== 测试 2: 文件系统加载器 ===");
        testFileSystemLoader();

        // 3. 测试类路径加载器
        System.out.println("\n=== 测试 3: 类路径加载器 ===");
        testClassPathLoader();

        // 4. 测试组合加载器
        System.out.println("\n=== 测试 4: 组合加载器（优先级测试）===");
        testCompositeLoader();

        // 5. 测试PromptManager
        System.out.println("\n=== 测试 5: PromptManager 门面类 ===");
        testPromptManager();

        // 6. 测试模板不存在的情况
        System.out.println("\n=== 测试 6: 异常处理测试 ===");
        testExceptionHandling();

        System.out.println("\n========================================");
        System.out.println("    所有测试执行完毕！");
        System.out.println("========================================");
    }

    /**
     * 测试模板渲染器
     */
    private static void testPromptRenderer() {
        PromptRenderer renderer = SimplePromptRenderer.builder().build();

        // 测试 1: 基本占位符替换
        String template1 = "你好，{{name}}！欢迎使用{{product}}。";
        Map<String, Object> params1 = new HashMap<>();
        params1.put("name", "张三");
        params1.put("product", "Sagent框架");
        String result1 = renderer.render(template1, params1);
        System.out.println("【基本替换】");
        System.out.println("  模板: " + template1);
        System.out.println("  参数: " + params1);
        System.out.println("  结果: " + result1);

        // 测试 2: 带默认值的占位符
        String template2 = "你好，{{name:用户}}！今天是{{date:未知日期}}。";
        Map<String, Object> params2 = new HashMap<>();
        params2.put("name", "李四");
        // date 未提供，使用默认值
        String result2 = renderer.render(template2, params2);
        System.out.println("\n【默认值测试】");
        System.out.println("  模板: " + template2);
        System.out.println("  参数: " + params2);
        System.out.println("  结果: " + result2);

        // 测试 3: 无参数渲染
        String template3 = "这是一个没有占位符的模板。";
        String result3 = renderer.render(template3);
        System.out.println("\n【无参数渲染】");
        System.out.println("  模板: " + template3);
        System.out.println("  结果: " + result3);

        // 测试 4: 复杂模板
        String template4 = """
                角色设定：{{role:AI助手}}
                任务描述：{{task:帮助用户解决问题}}
                
                用户输入：{{user_input}}
                
                请根据角色设定处理用户请求。
                """;
        Map<String, Object> params4 = new HashMap<>();
        params4.put("role", "专业编程助手");
        params4.put("user_input", "如何学习Java？");
        String result4 = renderer.render(template4, params4);
        System.out.println("\n【复杂模板】");
        System.out.println("  模板: " + template4.replace("\n", "\n         "));
        System.out.println("  参数: " + params4);
        System.out.println("  结果: \n" + result4);
    }

    /**
     * 测试文件系统加载器
     */
    private static void testFileSystemLoader() {
        // 创建文件系统加载器（从项目根目录的prompts文件夹加载）
        FileSystemPromptLoader loader = FileSystemPromptLoader.createDefault();

        System.out.println("加载器名称: " + loader.getName());
        System.out.println("基础路径: " + loader.getBasePath());

        // 检查示例模板是否存在
        boolean exists = loader.supports("example");
        System.out.println("'example' 模板是否存在: " + exists);

        if (exists) {
            loader.load("example").ifPresent(content -> {
                System.out.println("模板内容:\n" + content);
            });
        } else {
            System.out.println("提示：在项目根目录的 prompts/ 文件夹中创建 example.prompt 文件以测试文件系统加载器");
        }
    }

    /**
     * 测试类路径加载器
     */
    private static void testClassPathLoader() {
        // 创建类路径加载器
        ClassPathPromptLoader loader = ClassPathPromptLoader.builder().build();

        System.out.println("加载器名称: " + loader.getName());
        System.out.println("基础路径: " + loader.getBasePath());

        // 检查类路径模板是否存在
        boolean exists = loader.supports("classpath_demo");
        System.out.println("'classpath_demo' 模板是否存在: " + exists);

        if (exists) {
            loader.load("classpath_demo").ifPresent(content -> {
                System.out.println("模板内容:\n" + content);
            });
        } else {
            System.out.println("提示：在 resources/prompts/ 文件夹中创建 classpath_demo.prompt 文件以测试类路径加载器");
        }
    }

    /**
     * 测试组合加载器
     */
    private static void testCompositeLoader() {
        // 创建高优先级的自定义加载器（模拟）
        FileSystemPromptLoader highPriorityLoader = FileSystemPromptLoader.builder()
                .basePath(java.nio.file.Paths.get("./custom_prompts"))
                .name("HighPriorityLoader")
                .priority(200)
                .build();

        // 创建默认的文件系统加载器
        FileSystemPromptLoader defaultLoader = FileSystemPromptLoader.createDefault();

        // 创建类路径加载器
        ClassPathPromptLoader classPathLoader = ClassPathPromptLoader.builder().build();

        // 创建组合加载器（按优先级排序：高优先级 > 默认文件系统 > 类路径）
        CompositePromptLoader compositeLoader = CompositePromptLoader.builder()
                .loader(highPriorityLoader)
                .loader(defaultLoader)
                .loader(classPathLoader)
                .build();

        System.out.println("组合加载器名称: " + compositeLoader.getName());
        System.out.println("加载器优先级顺序:");
        System.out.println("  1. HighPriorityLoader (优先级 200)");
        System.out.println("  2. DefaultFileSystemPromptLoader (优先级 50)");
        System.out.println("  3. ClassPathPromptLoader (优先级 10)");

        // 测试检查模板存在性
        System.out.println("\n检查模板存在性:");
        System.out.println("  'example' 是否存在: " + compositeLoader.supports("example"));
        System.out.println("  'classpath_demo' 是否存在: " + compositeLoader.supports("classpath_demo"));
        System.out.println("  'not_exist' 是否存在: " + compositeLoader.supports("not_exist"));
    }

    /**
     * 测试 PromptManager 门面类
     */
    private static void testPromptManager() {
        // 使用默认配置创建管理器
        PromptManager manager = new PromptManager();
        System.out.println("PromptManager 初始化完成");
        System.out.println("使用的加载器: " + manager.getLoader().getName());

        // 检查模板存在性
        System.out.println("\n检查模板存在性:");
        System.out.println("  'example' 是否存在: " + manager.exists("example"));
        System.out.println("  'classpath_demo' 是否存在: " + manager.exists("classpath_demo"));

        // 尝试加载并渲染类路径模板
        if (manager.exists("classpath_demo")) {
            try {
                // 方式1: 使用 render 方法直接加载并渲染
                Map<String, Object> params = new HashMap<>();
                params.put("name", "王五");
                params.put("product", "Sagent");
                String result = manager.render("classpath_demo", params);
                System.out.println("\n【方式1】render 方法结果:\n" + result);

                // 方式2: 先加载模板，再多次渲染
                PromptTemplate template = manager.load("classpath_demo");
                String result2 = template.render(Map.of("name", "赵六", "product", "AI Agent"));
                System.out.println("\n【方式2】多次渲染 - 第一次:\n" + result2);

                // 方式3: 使用上下文渲染
                PromptRenderContext context = PromptRenderContext.of("name", "孙七")
                        .with("product", "智能体框架");
                String result3 = template.render(context);
                System.out.println("\n【方式3】上下文渲染:\n" + result3);

            } catch (Exception e) {
                System.out.println("渲染失败: " + e.getMessage());
            }
        }
    }

    /**
     * 测试异常处理
     */
    private static void testExceptionHandling() {
        PromptManager manager = new PromptManager();

        // 测试 1: tryLoad 方法（返回 Optional）
        System.out.println("【测试 tryLoad】");
        var optionalTemplate = manager.tryLoad("not_exist_template");
        System.out.println("  加载不存在的模板: " + (optionalTemplate.isPresent() ? "成功" : "失败（符合预期）"));

        // 测试 2: load 方法（抛出异常）
        System.out.println("\n【测试 load 异常】");
        try {
            manager.load("not_exist_template");
            System.out.println("  错误：应该抛出异常");
        } catch (SagentPromptException.TemplateNotFoundException e) {
            System.out.println("  捕获到预期异常: " + e.getMessage());
        }

        // 测试 3: render 不存在的模板
        System.out.println("\n【测试 render 异常】");
        try {
            manager.render("not_exist_template");
            System.out.println("  错误：应该抛出异常");
        } catch (SagentPromptException.TemplateNotFoundException e) {
            System.out.println("  捕获到预期异常: " + e.getMessage());
        }
    }
}
