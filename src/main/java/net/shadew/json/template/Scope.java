package net.shadew.json.template;

import net.shadew.json.JsonNode;

public class Scope {
    private final ScopeType type;
    private final JsonNode value;
    private JsonNode rootResult;

    public Scope(ScopeType type) {
        this.type = type;

        switch (type) {
            default:
                value = null;
                break;
            case ARRAY:
                value = JsonNode.array();
                break;
            case OBJECT:
                value = JsonNode.object();
                break;
        }
    }

    public void produceResult(JsonNode result) {
        if (type != ScopeType.ROOT)
            throw new IllegalArgumentException("Cannot create result for non-root scope, add to the value of the scope instead");
        rootResult = result;
    }

    public boolean requiresTermination() {
        return type == ScopeType.ROOT && rootResult != null;
    }

    public JsonNode rootResult() {
        return rootResult == null ? JsonNode.object() : rootResult;
    }

    public final ScopeType type() {
        return type;
    }

    public JsonNode value() {
        return value;
    }
}
