package dev.runefox.json.impl.node;

import dev.runefox.json.JsonNode;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArrayNode extends ConstructNode {
    private final List<JsonNode> children = new ArrayList<>();
    private Values values;

    public ArrayNode() {
    }

    public ArrayNode(int len) {
        while (len-- > 0)
            children.add(JsonNode.NULL);
    }

    public ArrayNode(JsonNode... nodes) {
        for (JsonNode node : nodes)
            add(node);
    }

    public ArrayNode(Iterable<? extends JsonNode> nodes) {
        for (JsonNode node : nodes)
            add(node);
    }

    @Override
    public boolean isObject() {
        return false;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public JsonNode get(int index) {
        requireMinSize(index + 1);
        return children.get(index);
    }

    @Override
    public JsonNode set(int index, JsonNode value) {
        requireMinSize(index + 1);
        children.set(index, JsonNode.orNull(value));
        return this;
    }

    @Override
    public JsonNode add(JsonNode value) {
        children.add(JsonNode.orNull(value));
        return this;
    }

    @Override
    public JsonNode insert(int index, JsonNode value) {
        requireMinSize(index);
        children.add(index, JsonNode.orNull(value));
        return this;
    }

    @Override
    public JsonNode remove(int index) {
        requireMinSize(index + 1);
        children.remove(index);
        return this;
    }

    @Override
    public int size() {
        return children.size();
    }

    @Override
    public JsonNode clear() {
        children.clear();
        return this;
    }

    @Override
    public JsonNode slice(int from, int to) {
        if (to < from)
            throw new IllegalArgumentException("Negative range");

        requireSize(to);
        while (size() > to) {
            remove(size() - 1);
        }
        while (size() > to - from) {
            remove(0);
        }
        return this;
    }

    @Override
    public JsonNode get(String key) {
        throw expectedType("OBJECT");
    }

    @Override
    public JsonNode set(String key, JsonNode value) {
        throw expectedType("OBJECT");
    }

    @Override
    public JsonNode remove(String key) {
        throw expectedType("OBJECT");
    }

    @Override
    public boolean has(String key) {
        throw expectedType("OBJECT");
    }

    @Override
    public boolean contains(JsonNode value) {
        return children.contains(value);
    }

    @Override
    public Set<String> keySet() {
        throw expectedType("OBJECT");
    }

    @Override
    public Collection<JsonNode> values() {
        if (values == null)
            values = new Values(children);
        return values;
    }

    @Override
    public Set<Map.Entry<String, JsonNode>> entrySet() {
        throw expectedType("OBJECT");
    }

    @Override
    public Stream<JsonNode> stream() {
        return children.stream();
    }

    @Override
    public void forEach(Consumer<? super JsonNode> action) {
        children.forEach(action);
    }

    @Override
    public void forEachEntry(BiConsumer<? super String, ? super JsonNode> fn) {
        throw expectedType("OBJECT");
    }

    @Override
    public JsonNode merge(JsonNode object) {
        throw expectedType("OBJECT");
    }

    @Override
    public JsonNode deepCopy() {
        JsonNode arr = JsonNode.array();
        forEach(n -> arr.add(n.deepCopy()));
        return arr;
    }

    @Override
    public JsonNode copy() {
        JsonNode arr = JsonNode.array();
        forEach(arr::add);
        return arr;
    }

    @Override
    protected String describeType() {
        return "ARRAY";
    }

    @Override
    public Iterator<JsonNode> iterator() {
        return children.iterator();
    }

    @Override
    public String toString() {
        return children.stream().map(Object::toString).collect(Collectors.joining(", ", "[", "]"));
    }
}
