package dev.runefox.json.impl.node;

import dev.runefox.json.JsonNode;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public abstract class PrimitiveNode extends AbstractNode {
    @Override
    public boolean isObject() {
        return false;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public JsonNode get(int index) {
        throw expectedType("ARRAY");
    }

    @Override
    public JsonNode set(int index, JsonNode value) {
        throw expectedType("ARRAY");
    }

    @Override
    public JsonNode add(JsonNode value) {
        throw expectedType("ARRAY");
    }

    @Override
    public JsonNode insert(int index, JsonNode value) {
        throw expectedType("ARRAY");
    }

    @Override
    public JsonNode remove(int index) {
        throw expectedType("ARRAY");
    }

    @Override
    public int size() {
        throw expectedType("ARRAY, OBJECT");
    }

    @Override
    public JsonNode clear() {
        throw expectedType("ARRAY, OBJECT");
    }

    @Override
    public JsonNode slice(int from, int to) {
        throw expectedType("ARRAY");
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
        throw expectedType("ARRAY, OBJECT");
    }

    @Override
    public Set<String> keySet() {
        throw expectedType("OBJECT");
    }

    @Override
    public Collection<JsonNode> values() {
        throw expectedType("ARRAY, OBJECT");
    }

    @Override
    public Set<Map.Entry<String, JsonNode>> entrySet() {
        throw expectedType("OBJECT");
    }

    @Override
    public Stream<JsonNode> stream() {
        throw expectedType("ARRAY, OBJECT");
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
        return this;
    }

    @Override
    public JsonNode copy() {
        return this;
    }

    @Override
    public Iterator<JsonNode> iterator() {
        throw expectedType("ARRAY");
    }
}
