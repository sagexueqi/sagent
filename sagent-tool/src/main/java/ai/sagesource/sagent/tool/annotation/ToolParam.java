package ai.sagesource.sagent.tool.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Function Tool Parameter Annotation
 *
 * @author: sage.xue
 * @time: 2026/3/19
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ToolParam {
    String name();

    String description();

    String[] enumValues() default {};

    boolean required() default true;
}
