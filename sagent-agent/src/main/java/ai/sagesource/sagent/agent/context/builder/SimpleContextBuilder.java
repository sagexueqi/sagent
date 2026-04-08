package ai.sagesource.sagent.agent.context.builder;

import ai.sagesource.sagent.agent.context.AgentContext;
import ai.sagesource.sagent.agent.context.SimpleAgentContext;

/**
 * 简单ContextBuilder实现
 * 每次调用都创建新的空Context，不维护历史状态
 * 适用于无状态、单轮对话场景
 *
 * @author: sage.xue
 * @time: 2026/4/6
 */
public class SimpleContextBuilder extends AbstractContextBuilder {

    @Override
    protected AgentContext doBuild(String contextId) {
        return SimpleAgentContext.create(contextId);
    }

    @Override
    public void save(AgentContext context) {
        // 简单实现不保存，直接丢弃
    }

    @Override
    public void clear(String contextId) {
        // 简单实现无状态，无需清除
    }

    @Override
    public String name() {
        return "SimpleContextBuilder";
    }
}
