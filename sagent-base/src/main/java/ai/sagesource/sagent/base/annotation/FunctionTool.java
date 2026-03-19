package ai.sagesource.sagent.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Function Tool Annotation
 *
 * @author: sage.xue
 * @time: 2026/3/19
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FunctionTool {

    String name();

    String description();
}
