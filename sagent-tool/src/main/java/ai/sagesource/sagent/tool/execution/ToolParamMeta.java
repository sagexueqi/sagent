package ai.sagesource.sagent.tool.execution;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 工具参数元数据，由框架在注册时从 @ToolParam 注解中解析得到。
 *
 * @author: sage.xue
 * @time: 2026/3/29
 */
@Getter
@Builder
public class ToolParamMeta {

    /**
     * 参数名（对应 @ToolParam#name）
     */
    private final String name;

    /**
     * 参数描述（对应 @ToolParam#description）
     */
    private final String description;

    /**
     * JSON Schema 类型字符串，如 "string"、"integer"、"number"、"boolean"。
     * 由框架从 Java 方法参数类型映射得到。
     */
    private final String type;

    /**
     * 枚举值列表（对应 @ToolParam#enumValues，为空则不限枚举）
     */
    private final List<String> enumValues;

    /**
     * 是否必需（对应 @ToolParam#required）
     */
    private final boolean required;

    /**
     * Java 实际类型，用于反射时的参数类型转换
     */
    private final Class<?> javaType;
}
