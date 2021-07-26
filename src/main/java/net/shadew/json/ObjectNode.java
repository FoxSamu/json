package net.shadew.json;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

final class ObjectNode extends AbstractConstructNode {
    private final Map<String, JsonNode> children = new LinkedHashMap<>();

    ObjectNode() {
        super(JsonType.OBJECT);
    }

    ObjectNode(Map<? extends String, ? extends JsonNode> map) {
        this();
        children.putAll(map);
    }

    @Override
    public JsonNode get(int index) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode set(int index, JsonNode value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode set(int index, String value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode set(int index, Number value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode set(int index, Boolean value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode add(JsonNode value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode add(String value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode add(Number value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode add(Boolean value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode insert(int index, JsonNode value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode insert(int index, String value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode insert(int index, Number value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode insert(int index, Boolean value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode remove(int index) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public int size() {
        return children.size();
    }

    @Override
    public JsonNode get(String key) {
        if (key == null) key = "null";
        return children.get(key);
    }

    @Override
    public JsonNode set(String key, JsonNode value) {
        if (key == null) key = "null";
        children.put(key, JsonNode.orNull(value));
        return this;
    }

    @Override
    public JsonNode set(String key, String value) {
        if (key == null) key = "null";
        children.put(key, JsonNode.string(value));
        return this;
    }

    @Override
    public JsonNode set(String key, Number value) {
        if (key == null) key = "null";
        children.put(key, JsonNode.number(value));
        return this;
    }

    @Override
    public JsonNode set(String key, Boolean value) {
        if (key == null) key = "null";
        children.put(key, JsonNode.bool(value));
        return this;
    }

    @Override
    public JsonNode remove(String key) {
        if (key == null) key = "null";
        children.remove(key);
        return this;
    }

    @Override
    public boolean has(String key) {
        if (key == null) key = "null";
        return children.containsKey(key);
    }

    @Override
    public Set<String> keys() {
        return children.keySet();
    }

    @Override
    public void forEachEntry(BiConsumer<? super String, ? super JsonNode> fn) {
        children.forEach(fn);
    }

    @Override
    public JsonNode deepCopy() {
        ObjectNode copy = new ObjectNode();
        for (Map.Entry<String, JsonNode> e : children.entrySet())
            copy.children.put(e.getKey(), e.getValue().deepCopy());
        return copy;
    }

    @Override
    public JsonNode copy() {
        return new ObjectNode(children);
    }

    @Override
    public Iterator<JsonNode> iterator() {
        return children.values().iterator();
    }

    @Override
    public JsonNode clear() {
        children.clear();
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        ObjectNode other = (ObjectNode) o;
        return children.equals(other.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(children);
    }

    @Override
    public String toString() {
        return "{" + children.entrySet().stream()
                             .map(e -> StringNode.quote(e.getKey()) + ": " + e.getValue())
                             .collect(Collectors.joining(", "))
                   + "}";
    }
}
