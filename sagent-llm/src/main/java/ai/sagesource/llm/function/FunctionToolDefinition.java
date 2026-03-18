package ai.sagesource.llm.function;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Function Tool Definition
 *
 * @author: sage.xue
 * @time: 2026/3/18
 */
@Getter
@Accessors(fluent = true)
public class FunctionToolDefinition {

    private String                    name;
    private String                    description;
    private List<ArgumentsDefinition> arguments;
    private List<String>              requiredArguments;

    @Getter
    @Accessors(fluent = true)
    public static class ArgumentsDefinition {
        private String       name;
        private String       type;
        private String       description;
        private List<String> enumValues;
    }
}
