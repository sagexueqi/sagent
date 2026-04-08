package ai.sagesource.sagent.agent.context.builder;

import ai.sagesource.sagent.agent.context.AgentContext;

import java.util.UUID;

/**
 * ContextBuilder抽象基类
 * 提供build方法的通用实现，子类只需实现save和clear
 * 适用于需要自定义存储策略的场景
 *
 * @author: sage.xue
 * @time: 2026/4/6
 */
public abstract class AbstractContextBuilder implements ContextBuilder {

    @Override
    public AgentContext build(String contextId) {
        String id = (contextId != null && !contextId.isEmpty()) 
                ? contextId 
                : UUID.randomUUID().toString();
        return doBuild(id);
    }

    /**
     * 子类实现具体的Context构建逻辑
     *
     * @param contextId 已处理好的上下文ID（非空）
     * @return AgentContext实例
     */
    protected abstract AgentContext doBuild(String contextId);

    @Override
    public abstract void save(AgentContext context);

    @Override
    public abstract void clear(String contextId);

    @Override
    public abstract String name();
}
