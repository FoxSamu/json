package net.shadew.json.template;

import net.shadew.json.JsonNode;

public interface Function {
    JsonNode call(TemplateContext ctx, Vfl vfl, JsonNode... nodes);
}
