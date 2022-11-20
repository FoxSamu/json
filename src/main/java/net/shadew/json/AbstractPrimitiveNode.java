package net.shadew.json;

import java.math.BigDecimal;
import java.math.BigInteger;
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
    public JsonType type(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public boolean isNull(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public boolean isString(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public boolean isNumber(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public boolean isBoolean(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public boolean isObject(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public boolean isArray(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public boolean isPrimitive(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public boolean isConstruct(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public boolean is(String key, JsonType type) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public boolean is(String key, JsonType... types) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode requireHas(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode requireNull(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode requireNotNull(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode requireString(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode requireNotString(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode requireNumber(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode requireNotNumber(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode requireBoolean(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode requireNotBoolean(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode requireObject(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode requireNotObject(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode requireArray(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode requireNotArray(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode requirePrimitive(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode requireNotPrimitive(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode requireConstruct(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode requireNotConstruct(String key) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode require(String key, JsonType type) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode requireNot(String key, JsonType type) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode require(String key, JsonType... types) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode requireNot(String key, JsonType... types) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode ifHas(String key, Consumer<JsonNode> action) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode ifString(String key, BiConsumer<JsonNode, String> action) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode ifNumber(String key, BiConsumer<JsonNode, Number> action) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode ifByte(String key, BiConsumer<JsonNode, Byte> action) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode ifShort(String key, BiConsumer<JsonNode, Short> action) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode ifInt(String key, BiConsumer<JsonNode, Integer> action) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode ifLong(String key, BiConsumer<JsonNode, Long> action) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode ifFloat(String key, BiConsumer<JsonNode, Float> action) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode ifDouble(String key, BiConsumer<JsonNode, Double> action) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode ifBigInteger(String key, BiConsumer<JsonNode, BigInteger> action) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode ifBigDecimal(String key, BiConsumer<JsonNode, BigDecimal> action) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode ifBoolean(String key, BiConsumer<JsonNode, Boolean> action) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode ifNull(String key, Consumer<JsonNode> action) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode ifArray(String key, Consumer<JsonNode> action) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode ifObject(String key, Consumer<JsonNode> action) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode ifPrimitive(String key, Consumer<JsonNode> action) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
    }

    @Override
    public JsonNode ifConstruct(String key, Consumer<JsonNode> action) {
        throw new IncorrectTypeException(type(), JsonType.OBJECT);
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
