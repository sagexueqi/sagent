package ai.sagesource.sagent.agent.context.builder;

import ai.sagesource.sagent.agent.context.AgentContext;
import ai.sagesource.sagent.agent.context.SimpleAgentContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存ContextBuilder实现
 * 在内存中按contextId维护对话历史
 * 适用于有状态、多轮对话场景
 *
 * @author: sage.xue
 * @time: 2026/4/6
 */
@Slf4j
public class MemoryContextBuilder extends AbstractContextBuilder {

    /**
     * 内存存储：contextId -> AgentContext
     */
    private final Map<String, AgentContext> contextStore = new ConcurrentHashMap<>();

    @Override
    protected AgentContext doBuild(String contextId) {
        // 如果存在已有上下文，返回已有实例；否则创建新的
        return contextStore.computeIfAbsent(contextId, key -> {
            log.debug("Creating new context for id: {}", key);
            return SimpleAgentContext.create(key);
        });
    }

    @Override
    public void save(AgentContext context) {
        if (context == null || context.contextId() == null) {
            return;
        }
        // 已在build时存入map，save操作确保状态一致性
        contextStore.put(context.contextId(), context);
        log.debug("Context saved for id: {}", context.contextId());
    }

    @Override
    public void clear(String contextId) {
        if (contextId != null) {
            contextStore.remove(contextId);
            log.debug("Context cleared for id: {}", contextId);
        }
    }

    /**
     * 获取指定上下文的当前状态（用于外部查询）
     */
    public AgentContext getContext(String contextId) {
        return contextStore.get(contextId);
    }

    /**
     * 检查上下文是否存在
     */
    public boolean hasContext(String contextId) {
        return contextStore.containsKey(contextId);
    }

    /**
     * 清除所有上下文
     */
    public void clearAll() {
        contextStore.clear();
        log.info("All contexts cleared");
    }

    @Override
    public String name() {
        return "MemoryContextBuilder";
    }
}
