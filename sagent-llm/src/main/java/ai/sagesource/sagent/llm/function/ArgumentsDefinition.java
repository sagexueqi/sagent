package ai.sagesource.sagent.llm.function;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Function Tool Arguments Definition
 *
 * @author: sage.xue
 * @time: 2026/3/19
 */
@Getter
@Accessors(fluent = true)
@Builder
public class ArgumentsDefinition {
    private String       name;
    private String       type;
    private String       description;
    private List<String> enumValues;
}
