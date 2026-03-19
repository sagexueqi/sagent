package ai.sagesource.sagent.tool.execution;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * Tool execution context
 *
 * @author: sage.xue
 * @time: 2026/3/19
 */
@Getter
@Builder
public class ToolExecutionContext {

    /**
     * The name of the tool to execute.
     */
    private final String toolName;

    /**
     * Tool parameters as a key-value map.
     */
    private final Map<String, Object> parameters;

    /**
     * Additional metadata for passing contextual information.
     * Can be used for traceId, user context, session info, etc.
     */
    private final Map<String, Object> metadata;

    /**
     * Get a parameter value with type safety.
     *
     * @param name parameter name
     * @param type expected type of the parameter
     * @param <T>  the type of the parameter
     * @return the parameter value, or null if not present
     * @throws IllegalArgumentException if the parameter type does not match
     */
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String name, Class<T> type) {
        Object value = parameters.get(name);
        if (value == null) {
            return null;
        }
        if (type.isInstance(value)) {
            return (T) value;
        }
        throw new IllegalArgumentException(
                "Parameter '" + name + "' type mismatch. Expected: " + type.getName() +
                        ", Actual: " + value.getClass().getName()
        );
    }

    /**
     * Get a parameter value with a default value fallback.
     *
     * @param name         parameter name
     * @param type         expected type of the parameter
     * @param defaultValue default value if parameter is not present
     * @param <T>          the type of the parameter
     * @return the parameter value, or defaultValue if not present
     */
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String name, Class<T> type, T defaultValue) {
        Object value = parameters.get(name);
        if (value == null) {
            return defaultValue;
        }
        if (type.isInstance(value)) {
            return (T) value;
        }
        return defaultValue;
    }

    /**
     * Get metadata value.
     *
     * @param key  metadata key
     * @param type expected type of the metadata value
     * @param <T>  the type of the metadata value
     * @return the metadata value, or null if not present
     */
    @SuppressWarnings("unchecked")
    public <T> T getMetadata(String key, Class<T> type) {
        if (metadata == null) {
            return null;
        }
        Object value = metadata.get(key);
        if (value == null) {
            return null;
        }
        if (type.isInstance(value)) {
            return (T) value;
        }
        throw new IllegalArgumentException(
                "Metadata '" + key + "' type mismatch. Expected: " + type.getName() +
                        ", Actual: " + value.getClass().getName()
        );
    }
}
