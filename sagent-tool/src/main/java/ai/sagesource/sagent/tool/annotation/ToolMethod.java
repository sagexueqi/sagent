package ai.sagesource.sagent.tool.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记工具的执行入口方法。
 * 若工具类中存在多个方法，或方法名不为 "execute"，则需要显式使用此注解标记执行入口。
 * 若未标注，框架默认查找名为 "execute" 的第一个公开方法。
 *
 * @author: sage.xue
 * @time: 2026/3/29
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ToolMethod {
}
