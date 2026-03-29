package ai.sagesource.sagent.tool.execution;

import ai.sagesource.sagent.tool.exception.SagentToolExecutionException;
import ai.sagesource.sagent.tool.exception.SagentToolNotFoundException;
import ai.sagesource.sagent.tool.registry.ToolRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 工具执行器。
 * 给定 ToolExecutionContext，从 ToolRegistry 查找对应工具，
 * 完成参数绑定（类型转换），通过反射调用执行方法，并包装返回结果。
 *
 * @author: sage.xue
 * @time: 2026/3/29
 */
public class ToolExecutor {

    private final ToolRegistry registry;

    public ToolExecutor(ToolRegistry registry) {
        this.registry = registry;
    }

    /**
     * 执行工具。
     *
     * @param context 工具执行上下文（包含工具名和参数）
     * @return 工具执行结果
     * @throws SagentToolNotFoundException  工具未注册
     * @throws SagentToolExecutionException 参数绑定失败或执行抛出异常
     */
    public ToolExecutionResult execute(ToolExecutionContext context) {
        ToolInvocation invocation = registry.find(context.getToolName())
                .orElseThrow(() -> new SagentToolNotFoundException(context.getToolName()));

        Object[] args = bindParameters(invocation, context);

        try {
            Object result = invocation.getExecuteMethod().invoke(invocation.getInstance(), args);
            // 若工具直接返回 ToolExecutionResult，直接使用；否则调用 toString() 包装
            if (result instanceof ToolExecutionResult toolResult) {
                return toolResult;
            }
            return ToolExecutionResult.success(result == null ? "" : result.toString());
        } catch (InvocationTargetException e) {
            // 目标方法内部抛出的业务异常
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            throw new SagentToolExecutionException(context.getToolName(),
                    "Tool execution failed: " + cause.getMessage(), cause);
        } catch (IllegalAccessException e) {
            throw new SagentToolExecutionException(context.getToolName(),
                    "Tool method is not accessible: " + e.getMessage(), e);
        }
    }

    /**
     * 将 ToolExecutionContext 中的参数（Map<String, Object>）按顺序绑定到方法参数。
     * LLM 返回的参数值通常为 String，此处完成到目标 Java 类型的转换。
     */
    private Object[] bindParameters(ToolInvocation invocation, ToolExecutionContext context) {
        Method method = invocation.getExecuteMethod();
        // 无参方法
        if (method.getParameterCount() == 0) {
            return new Object[0];
        }

        Object[] args = new Object[method.getParameterCount()];
        for (int i = 0; i < invocation.getParams().size(); i++) {
            ToolParamMeta paramMeta = invocation.getParams().get(i);
            Object rawValue = context.getParameters().get(paramMeta.getName());

            if (rawValue == null) {
                if (paramMeta.isRequired()) {
                    throw new SagentToolExecutionException(invocation.getName(),
                            "Required parameter '" + paramMeta.getName() + "' is missing");
                }
                args[i] = null;
            } else {
                args[i] = convertValue(rawValue, paramMeta.getJavaType(), invocation.getName(), paramMeta.getName());
            }
        }
        return args;
    }

    /**
     * 将原始值（通常为 String）转换为目标 Java 类型。
     */
    private Object convertValue(Object value, Class<?> targetType, String toolName, String paramName) {
        // 类型已匹配，直接返回
        if (targetType.isInstance(value)) {
            return value;
        }
        // 从 String 转换
        String strValue = value.toString();
        try {
            if (targetType == String.class) return strValue;
            if (targetType == Integer.class || targetType == int.class) return Integer.parseInt(strValue);
            if (targetType == Long.class || targetType == long.class) return Long.parseLong(strValue);
            if (targetType == Double.class || targetType == double.class) return Double.parseDouble(strValue);
            if (targetType == Float.class || targetType == float.class) return Float.parseFloat(strValue);
            if (targetType == Boolean.class || targetType == boolean.class) return Boolean.parseBoolean(strValue);
        } catch (NumberFormatException e) {
            throw new SagentToolExecutionException(toolName,
                    "Parameter '" + paramName + "' cannot be converted to " + targetType.getSimpleName() +
                            ": " + strValue, e);
        }
        throw new SagentToolExecutionException(toolName,
                "Unsupported parameter type for '" + paramName + "': " + targetType.getName());
    }
}
