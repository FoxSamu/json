package net.shadew.json;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

final class ArrayNode extends AbstractConstructNode {
    private final List<JsonNode> children = new ArrayList<>();

    ArrayNode() {
        super(JsonType.ARRAY);
    }

    ArrayNode(JsonNode... nodes) {
        this();
        for (JsonNode node : nodes)
            add(node);
    }

    ArrayNode(Iterable<? extends JsonNode> nodes) {
        this();
        for (JsonNode node : nodes)
            add(node);
    }

    @Override
    public JsonNode get(int index) {
        if (index < 0) index += size();
        return children.get(index);
    }

    @Override
    public JsonNode set(int index, JsonNode value) {
        if (index < 0) index += size();
        children.set(index, JsonNode.orNull(value));
        return this;
    }

    @Override
    public JsonNode set(int index, String value) {
        if (index < 0) index += size();
        children.set(index, JsonNode.string(value));
        return this;
    }

    @Override
    public JsonNode set(int index, Number value) {
        if (index < 0) index += size();
        children.set(index, JsonNode.number(value));
        return this;
    }

    @Override
    public JsonNode set(int index, Boolean value) {
        if (index < 0) index += size();
        children.set(index, JsonNode.bool(value));
        return this;
    }

    @Override
    public JsonNode add(JsonNode value) {
        children.add(JsonNode.orNull(value));
        return this;
    }

    @Override
    public JsonNode add(String value) {
        children.add(JsonNode.string(value));
        return this;
    }

    @Override
    public JsonNode add(Number value) {
        children.add(JsonNode.number(value));
        return this;
    }

    @Override
    public JsonNode add(Boolean value) {
        children.add(JsonNode.bool(value));
        return this;
    }

    @Override
    public JsonNode insert(int index, JsonNode value) {
        if (index < 0) index += size();
        children.add(index, JsonNode.orNull(value));
        return this;
    }

    @Override
    public JsonNode insert(int index, String value) {
        if (index < 0) index += size();
        children.add(index, JsonNode.string(value));
        return this;
    }

    @Override
    public JsonNode insert(int index, Number value) {
        if (index < 0) index += size();
        children.add(index, JsonNode.number(value));
        return this;
    }

    @Override
    public JsonNode insert(int index, Boolean value) {
        if (index < 0) index += size();
        children.add(index, JsonNode.bool(value));
        return this;
    }

    @Override
    public JsonNode remove(int index) {
        if (index < 0) index += size();
        return children.remove(index);
    }

    @Override
    public int size() {
        return children.size();
    }

    @Override
    public JsonNode get(String key) {
        throw new IncorrectTypeException(JsonType.ARRAY, JsonType.OBJECT);
    }

    @Override
    public JsonNode set(String key, JsonNode value) {
        throw new IncorrectTypeException(JsonType.ARRAY, JsonType.OBJECT);
    }

    @Override
    public JsonNode set(String key, String value) {
        throw new IncorrectTypeException(JsonType.ARRAY, JsonType.OBJECT);
    }

    @Override
    public JsonNode set(String key, Number value) {
        throw new IncorrectTypeException(JsonType.ARRAY, JsonType.OBJECT);
    }

    @Override
    public JsonNode set(String key, Boolean value) {
        throw new IncorrectTypeException(JsonType.ARRAY, JsonType.OBJECT);
    }

    @Override
    public JsonNode remove(String key) {
        throw new IncorrectTypeException(JsonType.ARRAY, JsonType.OBJECT);
    }

    @Override
    public boolean has(String key) {
        throw new IncorrectTypeException(JsonType.ARRAY, JsonType.OBJECT);
    }

    @Override
    public Set<String> keys() {
        throw new IncorrectTypeException(JsonType.ARRAY, JsonType.OBJECT);
    }

    @Override
    public void forEachEntry(BiConsumer<? super String, ? super JsonNode> fn) {
        throw new IncorrectTypeException(JsonType.ARRAY, JsonType.OBJECT);
    }

    @Override
    public JsonNode deepCopy() {
        ArrayNode copy = new ArrayNode();
        for (JsonNode node : children)
            copy.children.add(node.deepCopy());
        return copy;
    }

    @Override
    public JsonNode copy() {
        return new ArrayNode(children);
    }

    @Override
    public Iterator<JsonNode> iterator() {
        return children.iterator();
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

        ArrayNode other = (ArrayNode) o;
        return children.equals(other.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(children);
    }

    @Override
    public String toString() {
        return "[" + children.stream().map(JsonNode::toString).collect(Collectors.joining(", ")) + "]";
    }
}
