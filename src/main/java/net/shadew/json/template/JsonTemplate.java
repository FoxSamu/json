package net.shadew.json.template;

import net.shadew.json.JsonNode;

public interface JsonTemplate {
    JsonNode evaluate(TemplateContext context);
}
