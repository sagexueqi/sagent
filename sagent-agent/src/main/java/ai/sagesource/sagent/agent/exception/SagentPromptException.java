package ai.sagesource.sagent.agent.exception;

import ai.sagesource.sagent.base.exception.SagentException;

/**
 * 提示词相关异常
 *
 * @author: sage.xue
 * @time: 2026/3/30
 */
public class SagentPromptException extends SagentException {

    public SagentPromptException(String message) {
        super(message);
    }

    public SagentPromptException(String message, Throwable cause) {
        super(message, cause);
    }

    public SagentPromptException(Throwable cause) {
        super(cause);
    }

    /**
     * 模板不存在异常
     */
    public static class TemplateNotFoundException extends SagentPromptException {
        public TemplateNotFoundException(String templateName) {
            super("Template not found: " + templateName);
        }
    }

    /**
     * 模板渲染异常
     */
    public static class TemplateRenderException extends SagentPromptException {
        public TemplateRenderException(String message) {
            super("Failed to render template: " + message);
        }

        public TemplateRenderException(String message, Throwable cause) {
            super("Failed to render template: " + message, cause);
        }
    }
}
