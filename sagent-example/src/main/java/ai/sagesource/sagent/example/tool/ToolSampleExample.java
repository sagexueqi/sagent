package ai.sagesource.sagent.example.tool;

import ai.sagesource.sagent.tool.annotation.Tool;
import ai.sagesource.sagent.tool.annotation.ToolParam;
import ai.sagesource.sagent.tool.execution.ToolExecutionContext;
import ai.sagesource.sagent.tool.execution.ToolExecutionResult;
import ai.sagesource.sagent.tool.execution.ToolExecutor;
import ai.sagesource.sagent.tool.registry.ToolRegistry;

import java.util.Map;

/**
 * 工具体系示例：工具定义、注册、执行
 *
 * @author: sage.xue
 * @time: 2026/3/29
 */
public class ToolSampleExample {

    @Tool(name = "calculator", description = "执行基础四则运算，支持加减乘除")
    public static class CalculatorTool {

        public ToolExecutionResult execute(
                @ToolParam(name = "a", description = "第一个操作数") double a,
                @ToolParam(name = "b", description = "第二个操作数") double b,
                @ToolParam(name = "operator", description = "运算符",
                        enumValues = {"+", "-", "*", "/"}) String operator
        ) {
            double result = switch (operator) {
                case "+" -> a + b;
                case "-" -> a - b;
                case "*" -> a * b;
                case "/" -> {
                    if (b == 0) yield Double.NaN;
                    yield a / b;
                }
                default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
            };
            if (Double.isNaN(result)) {
                return ToolExecutionResult.failure("除数不能为零");
            }
            return ToolExecutionResult.success(
                    String.format("%s %s %s = %s", a, operator, b,
                            result == Math.floor(result) ? String.valueOf((long) result) : String.valueOf(result))
            );
        }
    }

    public static void main(String[] args) {
        // 1. 注册
        ToolRegistry registry = new ToolRegistry();
        registry.register(new CalculatorTool());

        // 2. 执行
        ToolExecutor executor = new ToolExecutor(registry);
        ToolExecutionResult result = executor.execute(
                ToolExecutionContext.builder()
                        .toolName("calculator")
                        // 模拟 LLM 返回的字符串参数，框架自动转换为 double
                        .parameters(Map.of("a", "12", "b", "4", "operator", "*"))
                        .build()
        );

        // 3. 输出结果
        System.out.println("success=" + result.isSuccess());
        System.out.println("content=" + result.getContent());
    }
}
