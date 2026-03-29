package ai.sagesource.sagent.tool.execution;

import lombok.Getter;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 代表一个已注册的工具实例及其元数据。
 * 由 ToolRegistry 在注册阶段解析 @Tool / @ToolParam 注解后生成，
 * ToolExecutor 使用此对象完成反射调用。
 *
 * @author: sage.xue
 * @time: 2026/3/29
 */
@Getter
public class ToolInvocation {

    /** 工具名称（来自 @Tool#name）*/
    private final String name;

    /** 工具描述（来自 @Tool#description）*/
    private final String description;

    /** 工具实例 */
    private final Object instance;

    /** 执行入口方法（通过 @ToolMethod 或约定名 "execute" 定位）*/
    private final Method executeMethod;

    /** 有序的参数元数据列表，顺序与 executeMethod 的参数顺序一致 */
    private final List<ToolParamMeta> params;

    public ToolInvocation(String name, String description,
                          Object instance, Method executeMethod,
                          List<ToolParamMeta> params) {
        this.name = name;
        this.description = description;
        this.instance = instance;
        this.executeMethod = executeMethod;
        this.params = params;
    }
}
