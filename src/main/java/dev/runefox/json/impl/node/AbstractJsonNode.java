package dev.runefox.json.impl.node;

import dev.runefox.json.IncorrectSizeException;
import dev.runefox.json.IncorrectTypeException;
import dev.runefox.json.JsonNode;
import dev.runefox.json.NodeType;
import dev.runefox.json.impl.Internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class AbstractJsonNode implements JsonNode {
    private final NodeType type;

    protected AbstractJsonNode(NodeType type) {
        this.type = type;
    }

    @Override
    public NodeType type() {
        return type;
    }

    @Override
    public boolean isNull() {
        return is(NodeType.NULL);
    }

    @Override
    public boolean isString() {
        return is(NodeType.STRING);
    }

    @Override
    public boolean isNumber() {
        return is(NodeType.NUMBER);
    }

    @Override
    public boolean isBoolean() {
        return is(NodeType.BOOLEAN);
    }

    @Override
    public boolean isObject() {
        return is(NodeType.OBJECT);
    }

    @Override
    public boolean isArray() {
        return is(NodeType.ARRAY);
    }

    @Override
    public boolean is(NodeType type) {
        return type == this.type;
    }

    @Override
    public boolean is(NodeType... types) {
        for (NodeType type : types)
            if (is(type)) return true;
        return false;
    }

    @Override
    public JsonNode requireNull() {
        require(NodeType.NULL);
        return this;
    }

    @Override
    public JsonNode requireNotNull() {
        requireNot(NodeType.NULL);
        return this;
    }

    @Override
    public JsonNode requireString() {
        require(NodeType.STRING);
        return this;
    }

    @Override
    public JsonNode requireNotString() {
        requireNot(NodeType.STRING);
        return this;
    }

    @Override
    public JsonNode requireNumber() {
        require(NodeType.NUMBER);
        return this;
    }

    @Override
    public JsonNode requireNotNumber() {
        requireNot(NodeType.NUMBER);
        return this;
    }

    @Override
    public JsonNode requireBoolean() {
        require(NodeType.BOOLEAN);
        return this;
    }

    @Override
    public JsonNode requireNotBoolean() {
        requireNot(NodeType.BOOLEAN);
        return this;
    }

    @Override
    public JsonNode requireObject() {
        require(NodeType.OBJECT);
        return this;
    }

    @Override
    public JsonNode requireNotObject() {
        requireNot(NodeType.OBJECT);
        return this;
    }

    @Override
    public JsonNode requireArray() {
        require(NodeType.ARRAY);
        return this;
    }

    @Override
    public JsonNode requireNotArray() {
        requireNot(NodeType.ARRAY);
        return this;
    }

    @Override
    public JsonNode require(NodeType type) {
        if (!is(type))
            throw new IncorrectTypeException(this.type, type);
        return this;
    }

    @Override
    public JsonNode requireNot(NodeType type) {
        if (is(type))
            throw new IncorrectTypeException(this.type, Internal.allExcluding0(type));
        return this;
    }

    @Override
    public JsonNode require(NodeType... types) {
        if (!is(types))
            throw new IncorrectTypeException(type, types);
        return this;
    }

    @Override
    public JsonNode requireNot(NodeType... types) {
        if (is(types))
            throw new IncorrectTypeException(Internal.makeMessageInv(type, types));
        return this;
    }

    @Override
    public JsonNode ifString(BiConsumer<JsonNode, String> action) {
        return this;
    }

    @Override
    public JsonNode ifNumber(BiConsumer<JsonNode, Number> action) {
        return this;
    }

    @Override
    public JsonNode ifByte(BiConsumer<JsonNode, Byte> action) {
        return this;
    }

    @Override
    public JsonNode ifShort(BiConsumer<JsonNode, Short> action) {
        return this;
    }

    @Override
    public JsonNode ifInt(BiConsumer<JsonNode, Integer> action) {
        return this;
    }

    @Override
    public JsonNode ifLong(BiConsumer<JsonNode, Long> action) {
        return this;
    }

    @Override
    public JsonNode ifFloat(BiConsumer<JsonNode, Float> action) {
        return this;
    }

    @Override
    public JsonNode ifDouble(BiConsumer<JsonNode, Double> action) {
        return this;
    }

    @Override
    public JsonNode ifBigInteger(BiConsumer<JsonNode, BigInteger> action) {
        return this;
    }

    @Override
    public JsonNode ifBigDecimal(BiConsumer<JsonNode, BigDecimal> action) {
        return this;
    }

    @Override
    public JsonNode ifBoolean(BiConsumer<JsonNode, Boolean> action) {
        return this;
    }

    @Override
    public JsonNode ifNull(Consumer<JsonNode> action) {
        return this;
    }

    @Override
    public JsonNode ifArray(Consumer<JsonNode> action) {
        return this;
    }

    @Override
    public JsonNode ifObject(Consumer<JsonNode> action) {
        return this;
    }

    @Override
    public JsonNode ifPrimitive(Consumer<JsonNode> action) {
        return this;
    }

    @Override
    public JsonNode ifConstruct(Consumer<JsonNode> action) {
        return this;
    }

    private static void require(int i, JsonNode node, NodeType type) {
        if (!node.is(type))
            throw new IncorrectTypeException(i, node.type(), type);
    }

    @Override
    public String[] asStringArray() {
        requireArray();
        int l = size();
        String[] arr = new String[l];
        for (int i = 0; i < l; i++) {
            JsonNode element = get(i);
            require(i, element, NodeType.STRING);
            arr[i] = get(i).asExactString();
        }
        return arr;
    }

    @Override
    public byte[] asByteArray() {
        requireArray();
        int l = size();
        byte[] arr = new byte[l];
        for (int i = 0; i < l; i++) {
            JsonNode element = get(i);
            require(i, element, NodeType.NUMBER);
            arr[i] = get(i).asByte();
        }
        return arr;
    }

    @Override
    public short[] asShortArray() {
        requireArray();
        int l = size();
        short[] arr = new short[l];
        for (int i = 0; i < l; i++) {
            JsonNode element = get(i);
            require(i, element, NodeType.NUMBER);
            arr[i] = get(i).asShort();
        }
        return arr;
    }

    @Override
    public int[] asIntArray() {
        requireArray();
        int l = size();
        int[] arr = new int[l];
        for (int i = 0; i < l; i++) {
            JsonNode element = get(i);
            require(i, element, NodeType.NUMBER);
            arr[i] = get(i).asInt();
        }
        return arr;
    }

    @Override
    public long[] asLongArray() {
        requireArray();
        int l = size();
        long[] arr = new long[l];
        for (int i = 0; i < l; i++) {
            JsonNode element = get(i);
            require(i, element, NodeType.NUMBER);
            arr[i] = get(i).asLong();
        }
        return arr;
    }

    @Override
    public float[] asFloatArray() {
        requireArray();
        int l = size();
        float[] arr = new float[l];
        for (int i = 0; i < l; i++) {
            JsonNode element = get(i);
            require(i, element, NodeType.NUMBER);
            arr[i] = get(i).asFloat();
        }
        return arr;
    }

    @Override
    public double[] asDoubleArray() {
        requireArray();
        int l = size();
        double[] arr = new double[l];
        for (int i = 0; i < l; i++) {
            JsonNode element = get(i);
            require(i, element, NodeType.NUMBER);
            arr[i] = get(i).asDouble();
        }
        return arr;
    }

    @Override
    public BigInteger[] asBigIntegerArray() {
        requireArray();
        int l = size();
        BigInteger[] arr = new BigInteger[l];
        for (int i = 0; i < l; i++) {
            JsonNode element = get(i);
            require(i, element, NodeType.NUMBER);
            arr[i] = get(i).asBigInteger();
        }
        return arr;
    }

    @Override
    public BigDecimal[] asBigDecimalArray() {
        requireArray();
        int l = size();
        BigDecimal[] arr = new BigDecimal[l];
        for (int i = 0; i < l; i++) {
            JsonNode element = get(i);
            require(i, element, NodeType.NUMBER);
            arr[i] = get(i).asBigDecimal();
        }
        return arr;
    }

    @Override
    public Number[] asNumberArray() {
        requireArray();
        int l = size();
        Number[] arr = new Number[l];
        for (int i = 0; i < l; i++) {
            JsonNode element = get(i);
            require(i, element, NodeType.NUMBER);
            arr[i] = get(i).asNumber();
        }
        return arr;
    }

    @Override
    public boolean[] asBooleanArray() {
        requireArray();
        int l = size();
        boolean[] arr = new boolean[l];
        for (int i = 0; i < l; i++) {
            JsonNode element = get(i);
            require(i, element, NodeType.BOOLEAN);
            arr[i] = get(i).asBoolean();
        }
        return arr;
    }

    @Override
    public JsonNode requireSize(int length) {
        require(NodeType.ARRAY, NodeType.OBJECT);
        int s = size();
        if (s != length)
            throw new IncorrectSizeException(s, length);
        return this;
    }

    @Override
    public JsonNode requireMinSize(int length) {
        require(NodeType.ARRAY, NodeType.OBJECT);
        int s = size();
        if (s < length)
            throw new IncorrectSizeException(s, length);
        return this;
    }

    @Override
    public JsonNode requireMaxSize(int length) {
        require(NodeType.ARRAY, NodeType.OBJECT);
        int s = size();
        if (s > length)
            throw new IncorrectSizeException(s, length);
        return this;
    }

    @Override
    public JsonNode requireSize(int min, int max) {
        require(NodeType.ARRAY, NodeType.OBJECT);
        int s = size();
        if (s < min || s > max)
            throw new IncorrectSizeException(s, min, max);
        return this;
    }

    @Override
    public String[] asStringArray(int fixedLength) {
        requireArray();
        requireSize(fixedLength);
        return asStringArray();
    }

    @Override
    public byte[] asByteArray(int fixedLength) {
        requireArray();
        requireSize(fixedLength);
        return asByteArray();
    }

    @Override
    public short[] asShortArray(int fixedLength) {
        requireArray();
        requireSize(fixedLength);
        return asShortArray();
    }

    @Override
    public int[] asIntArray(int fixedLength) {
        requireArray();
        requireSize(fixedLength);
        return asIntArray();
    }

    @Override
    public long[] asLongArray(int fixedLength) {
        requireArray();
        requireSize(fixedLength);
        return asLongArray();
    }

    @Override
    public float[] asFloatArray(int fixedLength) {
        requireArray();
        requireSize(fixedLength);
        return asFloatArray();
    }

    @Override
    public double[] asDoubleArray(int fixedLength) {
        requireArray();
        requireSize(fixedLength);
        return asDoubleArray();
    }

    @Override
    public BigInteger[] asBigIntegerArray(int fixedLength) {
        requireArray();
        requireSize(fixedLength);
        return asBigIntegerArray();
    }

    @Override
    public BigDecimal[] asBigDecimalArray(int fixedLength) {
        requireArray();
        requireSize(fixedLength);
        return asBigDecimalArray();
    }

    @Override
    public Number[] asNumberArray(int fixedLength) {
        requireArray();
        requireSize(fixedLength);
        return asNumberArray();
    }

    @Override
    public boolean[] asBooleanArray(int fixedLength) {
        requireArray();
        requireSize(fixedLength);
        return asBooleanArray();
    }

    @Override
    public List<JsonNode> asList() {
        throw new IncorrectTypeException(type, NodeType.ARRAY);
    }

    @Override
    public Map<String, JsonNode> asMap() {
        throw new IncorrectTypeException(type, NodeType.OBJECT);
    }

    @Override
    @Deprecated
    public JsonNode query(String path) {
        return query(JsonPath.parse(path));
    }

    @Override
    @Deprecated
    public JsonNode query(JsonPath path) {
        return path.query(this);
    }

    @Override
    public JsonNode wrap() {
        return JsonNode.array(this);
    }

    @Override
    public JsonNode wrap(String key) {
        JsonNode obj = JsonNode.object();
        obj.set(key, this);
        return obj;
    }
}
