package net.shadew.json.template;

import net.shadew.json.JsonNode;

public interface TernaryOperator {
    JsonNode apply(JsonNode a, JsonNode b, JsonNode c);
}
