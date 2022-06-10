package net.shadew.json;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

abstract class AbstractPrimitiveNode extends AbstractJsonNode {
    protected AbstractPrimitiveNode(JsonType type) {
        super(type);
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }

    @Override
    public boolean isConstruct() {
        return false;
    }

    @Override
    public JsonNode requirePrimitive() {
        return this;
    }

    @Override
    public JsonNode requireNotPrimitive() {
        throw new IncorrectTypeException(type(), JsonType.NOT_PRIMITIVES);
    }

    @Override
    public JsonNode requireConstruct() {
        throw new IncorrectTypeException(type(), JsonType.CONSTRUCTS);
    }

    @Override
    public JsonNode requireNotConstruct() {
        return this;
    }

    @Override
    public JsonNode ifPrimitive(Consumer<JsonNode> action) {
        action.accept(this);
        return this;
    }

    @Override
    public JsonNode get(int index) {
        throw new IncorrectTypeException(type(), JsonType.ARRAY);
    }

    @Override
    public JsonNode set(int index, JsonNode value) {
        throw new IncorrectTypeException(type(), JsonType.ARRAY);
    }

    @Override
    public JsonNode set(int index, String value) {
        throw new IncorrectTypeException(type(), JsonType.ARRAY);
    }

    @Override
    public JsonNode set(int index, Number value) {
        throw new IncorrectTypeException(type(), JsonType.ARRAY);
    }

    @Override
    public JsonNode set(int index, Boolean value) {
        throw new IncorrectTypeException(type(), JsonType.ARRAY);
    }

    @Override
    public JsonNode add(JsonNode value) {
        throw new IncorrectTypeException(type(), JsonType.ARRAY);
    }

    @Override
    public JsonNode add(String value) {
        throw new IncorrectTypeException(type(), JsonType.ARRAY);
    }

    @Override
    public JsonNode add(Number value) {
        throw new IncorrectTypeException(type(), JsonType.ARRAY);
    }

    @Override
    public JsonNode add(Boolean value) {
        throw new IncorrectTypeException(type(), JsonType.ARRAY);
    }

    @Override
    public JsonNode insert(int index, JsonNode value) {
        throw new IncorrectTypeException(type(), JsonType.ARRAY);
    }

    @Override
    public JsonNode insert(int index, String value) {
        throw new IncorrectTypeException(type(), JsonType.ARRAY);
    }

    @Override
    public JsonNode insert(int index, Number value) {
        throw new IncorrectTypeException(type(), JsonType.ARRAY);
    }

    @Override
    public JsonNode insert(int index, Boolean value) {
        throw new IncorrectTypeException(type(), JsonType.ARRAY);
    }

    @Override
    public JsonNode remove(int index) {
        throw new IncorrectTypeException(type(), JsonType.ARRAY);
    }

    @Override
    public int size() {
        throw new IncorrectTypeException(type(), JsonType.CONSTRUCTS);
    }

    @Override
    public int length() {
        throw new IncorrectTypeException(type(), JsonType.WITH_LENGTH);
    }

    @Override
    public JsonNode get(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode set(String key, JsonNode value) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode set(String key, String value) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode set(String key, Number value) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode set(String key, Boolean value) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode remove(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public boolean has(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public boolean contains(JsonNode value) {
        throw new IncorrectTypeException(type(), JsonType.CONSTRUCTS);
    }

    @Override
    public Set<String> keySet() {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public Collection<JsonNode> values() {
        throw new IncorrectTypeException(type(), JsonType.CONSTRUCTS);
    }

    @Override
    public Set<Map.Entry<String, JsonNode>> entrySet() {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public Stream<JsonNode> stream() {
        throw new IncorrectTypeException(type(), JsonType.CONSTRUCTS);
    }

    @Override
    public void forEachEntry(BiConsumer<? super String, ? super JsonNode> fn) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public Iterator<JsonNode> iterator() {
        throw new IncorrectTypeException(type(), JsonType.CONSTRUCTS);
    }

    @Override
    public JsonNode clear() {
        throw new IncorrectTypeException(type(), JsonType.CONSTRUCTS);
    }

    @Override
    public JsonNode append(JsonNode other) {
        throw new IncorrectTypeException(type(), JsonType.ARRAY);
    }

    @Override
    public JsonNode prepend(JsonNode other) {
        throw new IncorrectTypeException(type(), JsonType.ARRAY);
    }

    @Override
    public JsonNode slice(int from, int to) {
        throw new IncorrectTypeException(type(), JsonType.ARRAY);
    }

    @Override
    public JsonNode copy() {
        return this;
    }

    @Override
    public JsonNode deepCopy() {
        return this;
    }
}
