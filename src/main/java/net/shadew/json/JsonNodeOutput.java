package net.shadew.json;

import java.util.Stack;

public class JsonNodeOutput extends AbstractJsonOutput {
    private final Stack<JsonNode> output = new Stack<>();

    private JsonNode result;

    @Override
    protected void outputString(String value) {
        if (output.empty())
            output.push(JsonNode.string(value));
        else
            output.peek().add(value);
    }

    @Override
    protected void outputString(String key, String value) {
        output.peek().set(key, value);
    }

    @Override
    protected void outputNumber(Number value) {
        if (output.empty())
            output.push(JsonNode.number(value));
        else
            output.peek().add(value);
    }

    @Override
    protected void outputNumber(String key, Number value) {
        output.peek().set(key, value);
    }

    @Override
    protected void outputBoolean(Boolean value) {
        if (output.empty())
            output.push(JsonNode.bool(value));
        else
            output.peek().add(value);
    }

    @Override
    protected void outputBoolean(String key, Boolean value) {
        output.peek().set(key, value);
    }

    @Override
    protected void outputValue(JsonNode value) {
        if (output.empty())
            output.push(value);
        else
            output.peek().add(value);
    }

    @Override
    protected void outputValue(String key, JsonNode value) {
        output.peek().set(key, value);
    }

    @Override
    protected void outputNull() {
        if (output.empty())
            output.push(JsonNode.NULL);
        else
            output.peek().add(JsonNode.NULL);
    }

    @Override
    protected void outputNull(String key) {
        output.peek().set(key, JsonNode.NULL);
    }

    @Override
    protected void outputObjectStart() {
        JsonNode node = JsonNode.object();
        if (!output.empty())
            output.peek().add(node);
        output.push(node);
    }

    @Override
    protected void outputObjectStart(String key) {
        JsonNode node = JsonNode.object();
        output.peek().set(key, node);
        output.push(node);
    }

    @Override
    protected void outputObjectEnd() {
        JsonNode n = output.pop();
        if (output.empty())
            result = n;
    }

    @Override
    protected void outputArrayStart() {
        JsonNode node = JsonNode.array();
        if (!output.empty())
            output.peek().add(node);
        output.push(node);
    }

    @Override
    protected void outputArrayStart(String key) {
        JsonNode node = JsonNode.array();
        output.peek().set(key, node);
        output.push(node);
    }

    @Override
    protected void outputArrayEnd() {
        JsonNode n = output.pop();
        if (output.empty())
            result = n;
    }

    @Override
    protected void outputDocumentStart() {
        output.clear();
        result = null;
    }

    @Override
    protected void outputDocumentEnd() {
        if (result == null && !output.empty()) {
            result = output.pop();
        }
    }

    public JsonNode result() {
        return result;
    }
}
