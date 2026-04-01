package ai.sagesource.sagent.agent;

/**
 * Agent抽象接口
 * 定义所有Agent实现的基础契约
 *
 * @author: sage.xue
 * @time: 2026/3/31
 */
public interface Agent {

    /**
     * 获取Agent名称
     * 用于标识和区分不同的Agent实例
     *
     * @return Agent名称
     */
    String name();

    /**
     * 初始化Agent
     * 在Agent创建后调用，完成必要的初始化工作
     */
    void initialize();

    /**
     * 检查Agent是否已初始化
     *
     * @return true表示已初始化
     */
    boolean isInitialized();
}
