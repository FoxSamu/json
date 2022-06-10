package net.shadew.json.template;

import net.shadew.json.JsonNode;

public interface TemplateFunction {
    JsonNode call(TemplateContext ctx, VariableFunctionLayer vfl, JsonNode... nodes);
}
