package ai.sagesource.sagent.llm.openai.support;

import ai.sagesource.sagent.llm.function.FunctionToolDefinition;
import com.openai.core.JsonValue;
import com.openai.models.FunctionDefinition;
import com.openai.models.FunctionParameters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OpenAIFunctionDefinition Support
 *
 * @author: sage.xue
 * @time: 2026/3/22
 */
public class OpenAIFunctionDefinitionSupport {

    public static FunctionDefinition from(FunctionToolDefinition function) {
        // Parameter Definition
        FunctionParameters.Builder parameterBuilder = FunctionParameters.builder();
        parameterBuilder.putAdditionalProperty("type", JsonValue.from("object"));
        Map<String, Object> properties = new HashMap<>();
        if (function.arguments() != null && !function.arguments().isEmpty()) {
            function.arguments().forEach((argument) -> {
                Map<String, Object> argumentProperties = new HashMap<>();
                // type definition: all string
                argumentProperties.put("type", "string");
                // description definition
                argumentProperties.put("description", argument.description());
                // enum values
                if (argument.enumValues() != null && !argument.enumValues().isEmpty()) {
                    argumentProperties.put("enum", argument.enumValues());
                }

                properties.put(argument.name(), argumentProperties);
            });
        }
        parameterBuilder.putAdditionalProperty("properties", JsonValue.from(properties));
        parameterBuilder.putAdditionalProperty("required", JsonValue.from(function.requiredArguments()));

        // Function Definition
        return FunctionDefinition.builder()
                .name(function.name())
                .description(function.description())
                .parameters(parameterBuilder.build())
                .build();
    }

}
