package net.shadew.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class AbstractJsonNode implements JsonNode {
    private final JsonType type;

    protected AbstractJsonNode(JsonType type) {
        this.type = type;
    }

    @Override
    public JsonType type() {
        return type;
    }

    @Override
    public boolean isNull() {
        return is(JsonType.NULL);
    }

    @Override
    public boolean isString() {
        return is(JsonType.STRING);
    }

    @Override
    public boolean isNumber() {
        return is(JsonType.NUMBER);
    }

    @Override
    public boolean isBoolean() {
        return is(JsonType.BOOLEAN);
    }

    @Override
    public boolean isObject() {
        return is(JsonType.OBJECT);
    }

    @Override
    public boolean isArray() {
        return is(JsonType.ARRAY);
    }

    @Override
    public boolean is(JsonType type) {
        return type == this.type;
    }

    @Override
    public boolean is(JsonType... types) {
        for (JsonType type : types)
            if (is(type)) return true;
        return false;
    }

    @Override
    public JsonNode requireNull() {
        require(JsonType.NULL);
        return this;
    }

    @Override
    public JsonNode requireNotNull() {
        requireNot(JsonType.NULL);
        return this;
    }

    @Override
    public JsonNode requireString() {
        require(JsonType.STRING);
        return this;
    }

    @Override
    public JsonNode requireNotString() {
        requireNot(JsonType.STRING);
        return this;
    }

    @Override
    public JsonNode requireNumber() {
        require(JsonType.NUMBER);
        return this;
    }

    @Override
    public JsonNode requireNotNumber() {
        requireNot(JsonType.NUMBER);
        return this;
    }

    @Override
    public JsonNode requireBoolean() {
        require(JsonType.BOOLEAN);
        return this;
    }

    @Override
    public JsonNode requireNotBoolean() {
        requireNot(JsonType.BOOLEAN);
        return this;
    }

    @Override
    public JsonNode requireObject() {
        require(JsonType.OBJECT);
        return this;
    }

    @Override
    public JsonNode requireNotObject() {
        requireNot(JsonType.OBJECT);
        return this;
    }

    @Override
    public JsonNode requireArray() {
        require(JsonType.ARRAY);
        return this;
    }

    @Override
    public JsonNode requireNotArray() {
        requireNot(JsonType.ARRAY);
        return this;
    }

    @Override
    public JsonNode require(JsonType type) {
        if (!is(type))
            throw new IncorrectTypeException(this.type, type);
        return this;
    }

    @Override
    public JsonNode requireNot(JsonType type) {
        if (is(type))
            throw new IncorrectTypeException(this.type, JsonType.allExcluding0(type));
        return this;
    }

    @Override
    public JsonNode require(JsonType... types) {
        if (!is(types))
            throw new IncorrectTypeException(type, types);
        return this;
    }

    @Override
    public JsonNode requireNot(JsonType... types) {
        if (is(types))
            throw new IncorrectTypeException(null, type, types);
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

    private static void require(int i, JsonNode node, JsonType type) {
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
            require(i, element, JsonType.STRING);
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
            require(i, element, JsonType.NUMBER);
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
            require(i, element, JsonType.NUMBER);
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
            require(i, element, JsonType.NUMBER);
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
            require(i, element, JsonType.NUMBER);
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
            require(i, element, JsonType.NUMBER);
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
            require(i, element, JsonType.NUMBER);
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
            require(i, element, JsonType.NUMBER);
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
            require(i, element, JsonType.NUMBER);
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
            require(i, element, JsonType.NUMBER);
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
            require(i, element, JsonType.BOOLEAN);
            arr[i] = get(i).asBoolean();
        }
        return arr;
    }

    @Override
    public JsonNode requireSize(int length) {
        require(JsonType.ARRAY, JsonType.OBJECT);
        int s = size();
        if (s != length)
            throw new IncorrectSizeException(s, length);
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
        throw new IncorrectTypeException(type, JsonType.ARRAY);
    }

    @Override
    public Map<String, JsonNode> asMap() {
        throw new IncorrectTypeException(type, JsonType.OBJECT);
    }

    @Override
    public JsonNode query(String path) {
        return query(JsonPath.parse(path));
    }

    @Override
    public JsonNode query(JsonPath path) {
        return path.query(this);
    }
}
