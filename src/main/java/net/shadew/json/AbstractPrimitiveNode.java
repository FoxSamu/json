package net.shadew.json;

import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;

abstract class AbstractPrimitiveNode extends AbstractJsonNode {
    protected AbstractPrimitiveNode(JsonType type) {
        super(type);
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
        throw new IncorrectTypeException(type(), JsonType.ARRAY, JsonType.OBJECT);
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
    public Set<String> keys() {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public void forEachEntry(BiConsumer<? super String, ? super JsonNode> fn) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public Iterator<JsonNode> iterator() {
        throw new IncorrectTypeException(type(), JsonType.ARRAY, JsonType.OBJECT);
    }

    @Override
    public JsonNode clear() {
        throw new IncorrectTypeException(type(), JsonType.ARRAY, JsonType.OBJECT);
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
