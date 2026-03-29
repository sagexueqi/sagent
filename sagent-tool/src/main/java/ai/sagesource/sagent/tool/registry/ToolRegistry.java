package ai.sagesource.sagent.tool.registry;

import ai.sagesource.sagent.tool.annotation.Tool;
import ai.sagesource.sagent.tool.annotation.ToolMethod;
import ai.sagesource.sagent.tool.annotation.ToolParam;
import ai.sagesource.sagent.tool.exception.SagentToolRegistrationException;
import ai.sagesource.sagent.tool.execution.ToolInvocation;
import ai.sagesource.sagent.tool.execution.ToolParamMeta;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工具注册表。
 * 负责将标注了 @Tool 的工具实例解析成 ToolInvocation，并以工具名为键存储。
 * 线程安全（ConcurrentHashMap）。
 *
 * @author: sage.xue
 * @time: 2026/3/29
 */
public class ToolRegistry {

    private final Map<String, ToolInvocation> tools = new ConcurrentHashMap<>();

    // ——— 注册 ———

    /**
     * 注册单个工具实例。
     *
     * @throws SagentToolRegistrationException 若工具类未标注 @Tool、找不到执行方法、或工具名重复
     */
    public void register(Object toolInstance) {
        ToolInvocation invocation = resolve(toolInstance);
        if (tools.containsKey(invocation.getName())) {
            throw new SagentToolRegistrationException(
                    "Duplicate tool name: " + invocation.getName(),
                    toolInstance.getClass().getName()
            );
        }
        tools.put(invocation.getName(), invocation);
    }

    /**
     * 批量注册
     */
    public void registerAll(Collection<Object> toolInstances) {
        toolInstances.forEach(this::register);
    }

    // ——— 查询 ———

    public Optional<ToolInvocation> find(String toolName) {
        return Optional.ofNullable(tools.get(toolName));
    }

    public Collection<ToolInvocation> all() {
        return Collections.unmodifiableCollection(tools.values());
    }

    public boolean contains(String toolName) {
        return tools.containsKey(toolName);
    }

    // ——— 注销 ———

    public void unregister(String toolName) {
        tools.remove(toolName);
    }

    // ——— 内部解析逻辑 ———
    private ToolInvocation resolve(Object toolInstance) {
        Class<?> clazz          = toolInstance.getClass();
        Tool     toolAnnotation = clazz.getAnnotation(Tool.class);
        if (toolAnnotation == null) {
            throw new SagentToolRegistrationException(
                    "Class " + clazz.getName() + " is not annotated with @Tool",
                    clazz.getName()
            );
        }

        Method              executeMethod = findExecuteMethod(clazz);
        List<ToolParamMeta> params        = resolveParams(executeMethod, clazz.getName());

        return new ToolInvocation(
                toolAnnotation.name(),
                toolAnnotation.description(),
                toolInstance,
                executeMethod,
                params
        );
    }

    /**
     * 执行方法查找策略（优先级）：
     * 1. 查找标注了 @ToolMethod 的 public 方法（有且仅有一个）
     * 2. 查找名为 "execute" 的 public 方法（取第一个）
     */
    private Method findExecuteMethod(Class<?> clazz) {
        // 策略 1：@ToolMethod 注解
        List<Method> annotatedMethods = Arrays.stream(clazz.getMethods())
                .filter(m -> m.isAnnotationPresent(ToolMethod.class))
                .toList();

        if (annotatedMethods.size() > 1) {
            throw new SagentToolRegistrationException(
                    "Multiple @ToolMethod found in " + clazz.getName() + ", only one is allowed",
                    clazz.getName()
            );
        }
        if (annotatedMethods.size() == 1) {
            return annotatedMethods.get(0);
        }

        // 策略 2：约定名 "execute"
        return Arrays.stream(clazz.getMethods())
                .filter(m -> "execute".equals(m.getName()))
                .findFirst()
                .orElseThrow(() -> new SagentToolRegistrationException(
                        "No execute method found in " + clazz.getName() +
                                ". Either name your method 'execute' or annotate it with @ToolMethod",
                        clazz.getName()
                ));
    }

    private List<ToolParamMeta> resolveParams(Method method, String className) {
        Parameter[]         parameters = method.getParameters();
        List<ToolParamMeta> result     = new ArrayList<>(parameters.length);

        for (Parameter parameter : parameters) {
            ToolParam toolParam = parameter.getAnnotation(ToolParam.class);
            if (toolParam == null) {
                throw new SagentToolRegistrationException(
                        "Parameter '" + parameter.getName() + "' of method '" +
                                method.getName() + "' in " + className +
                                " is missing @ToolParam annotation",
                        className
                );
            }
            result.add(ToolParamMeta.builder()
                    .name(toolParam.name())
                    .description(toolParam.description())
                    .type(javaTypeToJsonSchemaType(parameter.getType()))
                    .enumValues(Arrays.asList(toolParam.enumValues()))
                    .required(toolParam.required())
                    .javaType(parameter.getType())
                    .build());
        }
        return result;
    }

    /**
     * Java 类型 → JSON Schema 类型映射
     */
    private String javaTypeToJsonSchemaType(Class<?> type) {
        if (type == String.class) return "string";
        if (type == Integer.class || type == int.class) return "integer";
        if (type == Long.class || type == long.class) return "integer";
        if (type == Double.class || type == double.class) return "number";
        if (type == Float.class || type == float.class) return "number";
        if (type == Boolean.class || type == boolean.class) return "boolean";
        // 其他复杂类型降级为 string
        return "string";
    }
}
