package ai.sagesource.sagent.agent.context.builder;

import ai.sagesource.sagent.agent.context.AgentContext;

/**
 * Context构建器接口
 * 定义如何构建Agent运行时的Context
 *
 * @author: sage.xue
 * @time: 2026/4/6
 */
public interface ContextBuilder {

    /**
     * 构建Context
     * Agent每次运行前调用此方法构建运行上下文
     *
     * @param contextId 上下文标识，可为null
     * @return AgentContext实例
     */
    AgentContext build(String contextId);

    /**
     * 保存Context
     * Agent运行结束后调用此方法保存上下文状态
     *
     * @param context 需要保存的上下文
     */
    void save(AgentContext context);

    /**
     * 清除指定上下文
     *
     * @param contextId 上下文标识
     */
    void clear(String contextId);

    /**
     * 获取构建器名称
     *
     * @return 构建器名称
     */
    String name();
}
