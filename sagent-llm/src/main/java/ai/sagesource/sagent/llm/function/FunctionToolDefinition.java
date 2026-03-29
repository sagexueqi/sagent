package ai.sagesource.sagent.llm.function;

import lombok.Builder;
import lombok.Data;
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
@Builder
public class FunctionToolDefinition {

    private String                    name;
    private String                    description;
    private List<ArgumentsDefinition> arguments;
    private List<String>              requiredArguments;
}
