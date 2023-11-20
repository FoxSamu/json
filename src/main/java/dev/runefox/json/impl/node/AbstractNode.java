package dev.runefox.json.impl.node;

import dev.runefox.json.*;
import dev.runefox.json.impl.Internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract non-sealed class AbstractNode implements JsonNode {
    protected abstract String describeType();

    protected IncorrectTypeException expectedType(String type) {
        return new IncorrectTypeException(describeType(), type);
    }

    @Override
    public boolean isPrimitive() {
        return !isConstruct();
    }

    @Override
    public boolean isConstruct() {
        return isObject() || isArray();
    }

    @Override
    public boolean isTemporal() {
        return isOffsetDateTime() || isLocalDateTime() || isLocalDate() || isLocalTime();
    }

    @Override
    public boolean is(NodeType type) {
        return type.check().test(this);
    }

    @Override
    public boolean is(NodeType... types) {
        for (NodeType n : types) {
            if (is(n))
                return true;
        }
        return false;
    }

    @Override
    public JsonNode requireNull() {
        return require(NodeType.NULL);
    }

    @Override
    public JsonNode requireNotNull() {
        return requireNot(NodeType.NULL);
    }

    @Override
    public JsonNode requireString() {
        return require(NodeType.STRING);
    }

    @Override
    public JsonNode requireNotString() {
        return requireNot(NodeType.STRING);
    }

    @Override
    public JsonNode requireNumber() {
        return require(NodeType.NUMBER);
    }

    @Override
    public JsonNode requireNotNumber() {
        return requireNot(NodeType.NUMBER);
    }

    @Override
    public JsonNode requireBoolean() {
        return require(NodeType.BOOLEAN);
    }

    @Override
    public JsonNode requireNotBoolean() {
        return requireNot(NodeType.BOOLEAN);
    }

    @Override
    public JsonNode requireObject() {
        return require(NodeType.OBJECT);
    }

    @Override
    public JsonNode requireNotObject() {
        return requireNot(NodeType.OBJECT);
    }

    @Override
    public JsonNode requireArray() {
        return require(NodeType.ARRAY);
    }

    @Override
    public JsonNode requireNotArray() {
        return requireNot(NodeType.ARRAY);
    }

    @Override
    public JsonNode requirePrimitive() {
        if (isPrimitive()) return this;
        throw new IncorrectTypeException(describeType(), "anything but ARRAY, OBJECT");
    }

    @Override
    public JsonNode requireNotPrimitive() {
        if (!isPrimitive()) return this;
        throw new IncorrectTypeException(describeType(), "ARRAY, OBJECT");
    }

    @Override
    public JsonNode requireConstruct() {
        return requireNotPrimitive();
    }

    @Override
    public JsonNode requireNotConstruct() {
        return requirePrimitive();
    }

    @Override
    public JsonNode requireOffsetDateTime() {
        return require(NodeType.OFFSET_DATE_TIME);
    }

    @Override
    public JsonNode requireNotOffsetDateTime() {
        return requireNot(NodeType.OFFSET_DATE_TIME);
    }

    @Override
    public JsonNode requireLocalDateTime() {
        return require(NodeType.LOCAL_DATE_TIME);
    }

    @Override
    public JsonNode requireNotLocalDateTime() {
        return requireNot(NodeType.LOCAL_DATE_TIME);
    }

    @Override
    public JsonNode requireLocalDate() {
        return require(NodeType.LOCAL_DATE);
    }

    @Override
    public JsonNode requireNotLocalDate() {
        return requireNot(NodeType.LOCAL_DATE);
    }

    @Override
    public JsonNode requireLocalTime() {
        return require(NodeType.LOCAL_DATE);
    }

    @Override
    public JsonNode requireNotLocalTime() {
        return requireNot(NodeType.LOCAL_DATE);
    }

    @Override
    public JsonNode requireTemporal() {
        if (isTemporal()) return this;
        throw new IncorrectTypeException(describeType(), "anything but TEMPORAL");
    }

    @Override
    public JsonNode requireNotTemporal() {
        if (!isTemporal()) return this;
        throw new IncorrectTypeException(describeType(), "TEMPORAL");
    }

    @Override
    public JsonNode require(NodeType type) {
        if (is(type)) return this;
        throw new IncorrectTypeException(describeType(), type.toString());
    }

    @Override
    public JsonNode requireNot(NodeType type) {
        if (!is(type)) return this;
        throw new IncorrectTypeException(describeType(), "anything but " + type.toString());
    }

    @Override
    public JsonNode require(NodeType... types) {
        if (is(types)) return this;
        throw new IncorrectTypeException(
            describeType(),
            Arrays.stream(types).map(NodeType::name).collect(Collectors.joining(", "))
        );
    }

    @Override
    public JsonNode requireNot(NodeType... types) {
        if (!is(types)) return this;
        throw new IncorrectTypeException(
            describeType(),
            Arrays.stream(types).map(NodeType::name).collect(Collectors.joining(", ", "anything but ", ""))
        );
    }

    @Override
    public JsonNode ifString(BiConsumer<JsonNode, String> action) {
        if (isString()) {
            action.accept(this, show());
        }
        return this;
    }

    @Override
    public JsonNode ifNumber(BiConsumer<JsonNode, Number> action) {
        if (isNumber()) {
            action.accept(this, asNumber());
        }
        return this;
    }

    @Override
    public JsonNode ifByte(BiConsumer<JsonNode, Byte> action) {
        if (isNumber()) {
            action.accept(this, asByte());
        }
        return this;
    }

    @Override
    public JsonNode ifShort(BiConsumer<JsonNode, Short> action) {
        if (isNumber()) {
            action.accept(this, asShort());
        }
        return this;
    }

    @Override
    public JsonNode ifInt(BiConsumer<JsonNode, Integer> action) {
        if (isNumber()) {
            action.accept(this, asInt());
        }
        return this;
    }

    @Override
    public JsonNode ifLong(BiConsumer<JsonNode, Long> action) {
        if (isNumber()) {
            action.accept(this, asLong());
        }
        return this;
    }

    @Override
    public JsonNode ifFloat(BiConsumer<JsonNode, Float> action) {
        if (isNumber()) {
            action.accept(this, asFloat());
        }
        return this;
    }

    @Override
    public JsonNode ifDouble(BiConsumer<JsonNode, Double> action) {
        if (isNumber()) {
            action.accept(this, asDouble());
        }
        return this;
    }

    @Override
    public JsonNode ifBigInteger(BiConsumer<JsonNode, BigInteger> action) {
        if (isNumber()) {
            action.accept(this, asBigInteger());
        }
        return this;
    }

    @Override
    public JsonNode ifBigDecimal(BiConsumer<JsonNode, BigDecimal> action) {
        if (isNumber()) {
            action.accept(this, asBigDecimal());
        }
        return this;
    }

    @Override
    public JsonNode ifBoolean(BiConsumer<JsonNode, Boolean> action) {
        if (isBoolean()) {
            action.accept(this, asBoolean());
        }
        return this;
    }

    @Override
    public JsonNode ifNull(Consumer<JsonNode> action) {
        if (isNull()) {
            action.accept(this);
        }
        return this;
    }

    @Override
    public JsonNode ifArray(Consumer<JsonNode> action) {
        if (isArray()) {
            action.accept(this);
        }
        return this;
    }

    @Override
    public JsonNode ifObject(Consumer<JsonNode> action) {
        if (isObject()) {
            action.accept(this);
        }
        return this;
    }

    @Override
    public JsonNode ifPrimitive(Consumer<JsonNode> action) {
        if (isPrimitive()) {
            action.accept(this);
        }
        return this;
    }

    @Override
    public JsonNode ifConstruct(Consumer<JsonNode> action) {
        if (isConstruct()) {
            action.accept(this);
        }
        return this;
    }

    @Override
    public JsonNode ifTemporal(BiConsumer<JsonNode, Temporal> action) {
        if (isTemporal()) {
            action.accept(this, asTemporal());
        }
        return this;
    }

    @Override
    public JsonNode ifOffsetDateTime(BiConsumer<JsonNode, OffsetDateTime> action) {
        if (isOffsetDateTime()) {
            action.accept(this, asOffsetDateTime());
        }
        return this;
    }

    @Override
    public JsonNode ifLocalDateTime(BiConsumer<JsonNode, LocalDateTime> action) {
        if (isLocalDateTime()) {
            action.accept(this, asLocalDateTime());
        }
        return this;
    }

    @Override
    public JsonNode ifLocalDate(BiConsumer<JsonNode, LocalDate> action) {
        if (isLocalDate()) {
            action.accept(this, asLocalDate());
        }
        return this;
    }

    @Override
    public JsonNode ifLocalTime(BiConsumer<JsonNode, LocalTime> action) {
        if (isLocalTime()) {
            action.accept(this, asLocalTime());
        }
        return this;
    }

    @Override
    public boolean hasNull(String key) {
        requireObject();
        return has(key) && get(key).isNull();
    }

    @Override
    public boolean hasString(String key) {
        requireObject();
        return has(key) && get(key).isString();
    }

    @Override
    public boolean hasNumber(String key) {
        requireObject();
        return has(key) && get(key).isNumber();
    }

    @Override
    public boolean hasBoolean(String key) {
        requireObject();
        return has(key) && get(key).isBoolean();
    }

    @Override
    public boolean hasObject(String key) {
        requireObject();
        return has(key) && get(key).isObject();
    }

    @Override
    public boolean hasArray(String key) {
        requireObject();
        return has(key) && get(key).isArray();
    }

    @Override
    public boolean hasPrimitive(String key) {
        requireObject();
        return has(key) && get(key).isPrimitive();
    }

    @Override
    public boolean hasConstruct(String key) {
        requireObject();
        return has(key) && get(key).isConstruct();
    }

    @Override
    public boolean hasTemporal(String key) {
        requireObject();
        return has(key) && get(key).isTemporal();
    }

    @Override
    public boolean hasOffsetDateTime(String key) {
        requireObject();
        return has(key) && get(key).isOffsetDateTime();
    }

    @Override
    public boolean hasLocalDateTime(String key) {
        requireObject();
        return has(key) && get(key).isLocalDateTime();
    }

    @Override
    public boolean hasLocalDate(String key) {
        requireObject();
        return has(key) && get(key).isLocalDate();
    }

    @Override
    public boolean hasLocalTime(String key) {
        requireObject();
        return has(key) && get(key).isLocalTime();
    }

    @Override
    public boolean has(String key, NodeType type) {
        requireObject();
        return has(key) && get(key).is(type);
    }

    @Override
    public boolean has(String key, NodeType... types) {
        requireObject();
        return has(key) && get(key).is(types);
    }

    @Override
    public JsonNode requireHas(String key) {
        requireObject();
        if (!has(key)) {
            throw new MissingKeyException(key);
        }
        return this;
    }

    @Override
    public JsonNode requireHasNull(String key) {
        requireHas(key);
        get(key).requireNull();
        return this;
    }

    @Override
    public JsonNode requireHasNotNull(String key) {
        requireHas(key);
        get(key).requireNotNull();
        return this;
    }

    @Override
    public JsonNode requireHasString(String key) {
        requireHas(key);
        get(key).requireString();
        return this;
    }

    @Override
    public JsonNode requireHasNotString(String key) {
        requireHas(key);
        get(key).requireNotString();
        return this;
    }

    @Override
    public JsonNode requireHasNumber(String key) {
        requireHas(key);
        get(key).requireNumber();
        return this;
    }

    @Override
    public JsonNode requireHasNotNumber(String key) {
        requireHas(key);
        get(key).requireNotNumber();
        return this;
    }

    @Override
    public JsonNode requireHasBoolean(String key) {
        requireHas(key);
        get(key).requireBoolean();
        return this;
    }

    @Override
    public JsonNode requireHasNotBoolean(String key) {
        requireHas(key);
        get(key).requireNotBoolean();
        return this;
    }

    @Override
    public JsonNode requireHasObject(String key) {
        requireHas(key);
        get(key).requireObject();
        return this;
    }

    @Override
    public JsonNode requireHasNotObject(String key) {
        requireHas(key);
        get(key).requireNotObject();
        return this;
    }

    @Override
    public JsonNode requireHasArray(String key) {
        requireHas(key);
        get(key).requireArray();
        return this;
    }

    @Override
    public JsonNode requireHasNotArray(String key) {
        requireHas(key);
        get(key).requireNotArray();
        return this;
    }

    @Override
    public JsonNode requireHasPrimitive(String key) {
        requireHas(key);
        get(key).requirePrimitive();
        return this;
    }

    @Override
    public JsonNode requireHasNotPrimitive(String key) {
        requireHas(key);
        get(key).requireNotPrimitive();
        return this;
    }

    @Override
    public JsonNode requireHasConstruct(String key) {
        requireHas(key);
        get(key).requireConstruct();
        return this;
    }

    @Override
    public JsonNode requireHasNotConstruct(String key) {
        requireHas(key);
        get(key).requireNotConstruct();
        return this;
    }

    @Override
    public JsonNode requireHasTemporal(String key) {
        requireHas(key);
        get(key).requireTemporal();
        return this;
    }

    @Override
    public JsonNode requireHasNotTemporal(String key) {
        requireHas(key);
        get(key).requireNotTemporal();
        return this;
    }

    @Override
    public JsonNode requireHasOffsetDateTime(String key) {
        requireHas(key);
        get(key).requireOffsetDateTime();
        return this;
    }

    @Override
    public JsonNode requireHasNotOffsetDateTime(String key) {
        requireHas(key);
        get(key).requireNotOffsetDateTime();
        return this;
    }

    @Override
    public JsonNode requireHasLocalDateTime(String key) {
        requireHas(key);
        get(key).requireLocalDateTime();
        return this;
    }

    @Override
    public JsonNode requireHasNotLocalDateTime(String key) {
        requireHas(key);
        get(key).requireNotLocalDateTime();
        return this;
    }

    @Override
    public JsonNode requireHasLocalDate(String key) {
        requireHas(key);
        get(key).requireLocalDate();
        return this;
    }

    @Override
    public JsonNode requireHasNotLocalDate(String key) {
        requireHas(key);
        get(key).requireNotLocalDate();
        return this;
    }

    @Override
    public JsonNode requireHasLocalTime(String key) {
        requireHas(key);
        get(key).requireLocalTime();
        return this;
    }

    @Override
    public JsonNode requireHasNotLocalTime(String key) {
        requireHas(key);
        get(key).requireNotLocalTime();
        return this;
    }

    @Override
    public JsonNode requireHas(String key, NodeType type) {
        requireHas(key);
        get(key).require(type);
        return this;
    }

    @Override
    public JsonNode requireHasNot(String key, NodeType type) {
        requireHas(key);
        get(key).requireNot(type);
        return this;
    }

    @Override
    public JsonNode requireHas(String key, NodeType... types) {
        requireHas(key);
        get(key).require(types);
        return this;
    }

    @Override
    public JsonNode requireHasNot(String key, NodeType... types) {
        requireHas(key);
        get(key).requireNot(types);
        return this;
    }

    @Override
    public JsonNode ifHas(String key, Consumer<JsonNode> action) {
        requireHas(key);
        if (has(key)) {
            action.accept(get(key));
        }
        return this;
    }

    @Override
    public JsonNode ifHasString(String key, BiConsumer<JsonNode, String> action) {
        requireHas(key);
        if (has(key)) {
            get(key).ifString(action);
        }
        return this;
    }

    @Override
    public JsonNode ifHasNumber(String key, BiConsumer<JsonNode, Number> action) {
        requireHas(key);
        if (has(key)) {
            get(key).ifNumber(action);
        }
        return this;
    }

    @Override
    public JsonNode ifHasByte(String key, BiConsumer<JsonNode, Byte> action) {
        requireHas(key);
        if (has(key)) {
            get(key).ifByte(action);
        }
        return this;
    }

    @Override
    public JsonNode ifHasShort(String key, BiConsumer<JsonNode, Short> action) {
        requireHas(key);
        if (has(key)) {
            get(key).ifShort(action);
        }
        return this;
    }

    @Override
    public JsonNode ifHasInt(String key, BiConsumer<JsonNode, Integer> action) {
        requireHas(key);
        if (has(key)) {
            get(key).ifInt(action);
        }
        return this;
    }

    @Override
    public JsonNode ifHasLong(String key, BiConsumer<JsonNode, Long> action) {
        requireHas(key);
        if (has(key)) {
            get(key).ifLong(action);
        }
        return this;
    }

    @Override
    public JsonNode ifHasFloat(String key, BiConsumer<JsonNode, Float> action) {
        requireHas(key);
        if (has(key)) {
            get(key).ifFloat(action);
        }
        return this;
    }

    @Override
    public JsonNode ifHasDouble(String key, BiConsumer<JsonNode, Double> action) {
        requireHas(key);
        if (has(key)) {
            get(key).ifDouble(action);
        }
        return this;
    }

    @Override
    public JsonNode ifHasBigInteger(String key, BiConsumer<JsonNode, BigInteger> action) {
        requireHas(key);
        if (has(key)) {
            get(key).ifBigInteger(action);
        }
        return this;
    }

    @Override
    public JsonNode ifHasBigDecimal(String key, BiConsumer<JsonNode, BigDecimal> action) {
        requireHas(key);
        if (has(key)) {
            get(key).ifBigDecimal(action);
        }
        return this;
    }

    @Override
    public JsonNode ifHasBoolean(String key, BiConsumer<JsonNode, Boolean> action) {
        requireHas(key);
        if (has(key)) {
            get(key).ifBoolean(action);
        }
        return this;
    }

    @Override
    public JsonNode ifHasNull(String key, Consumer<JsonNode> action) {
        requireHas(key);
        if (has(key)) {
            get(key).ifNull(action);
        }
        return this;
    }

    @Override
    public JsonNode ifHasArray(String key, Consumer<JsonNode> action) {
        requireHas(key);
        if (has(key)) {
            get(key).ifArray(action);
        }
        return this;
    }

    @Override
    public JsonNode ifHasObject(String key, Consumer<JsonNode> action) {
        requireHas(key);
        if (has(key)) {
            get(key).ifObject(action);
        }
        return this;
    }

    @Override
    public JsonNode ifHasPrimitive(String key, Consumer<JsonNode> action) {
        requireHas(key);
        if (has(key)) {
            get(key).ifPrimitive(action);
        }
        return this;
    }

    @Override
    public JsonNode ifHasConstruct(String key, Consumer<JsonNode> action) {
        requireHas(key);
        if (has(key)) {
            get(key).ifConstruct(action);
        }
        return this;
    }

    @Override
    public JsonNode ifHasTemporal(String key, BiConsumer<JsonNode, Temporal> action) {
        requireHas(key);
        if (has(key)) {
            get(key).ifTemporal(action);
        }
        return this;
    }

    @Override
    public JsonNode ifHasOffsetDateTime(String key, BiConsumer<JsonNode, OffsetDateTime> action) {
        requireHas(key);
        if (has(key)) {
            get(key).ifOffsetDateTime(action);
        }
        return this;
    }

    @Override
    public JsonNode ifHasLocalDateTime(String key, BiConsumer<JsonNode, LocalDateTime> action) {
        requireHas(key);
        if (has(key)) {
            get(key).ifLocalDateTime(action);
        }
        return this;
    }

    @Override
    public JsonNode ifHasLocalDate(String key, BiConsumer<JsonNode, LocalDate> action) {
        requireHas(key);
        if (has(key)) {
            get(key).ifLocalDate(action);
        }
        return this;
    }

    @Override
    public JsonNode ifHasLocalTime(String key, BiConsumer<JsonNode, LocalTime> action) {
        requireHas(key);
        if (has(key)) {
            get(key).ifLocalTime(action);
        }
        return this;
    }

    @Override
    public String[] showArray() {
        requireArray();
        String[] arr = new String[size()];
        for (int i = 0, l = size(); i < l; i++) {
            try {
                arr[i] = get(i).show();
            } catch (IncorrectTypeException exc) {
                throw Internal.withIndex(exc, i);
            }
        }
        return arr;
    }

    @Override
    public String[] asStringArray() {
        requireArray();
        String[] arr = new String[size()];
        for (int i = 0, l = size(); i < l; i++) {
            try {
                arr[i] = get(i).asString();
            } catch (IncorrectTypeException exc) {
                throw Internal.withIndex(exc, i);
            }
        }
        return arr;
    }

    @Override
    public byte[] asByteArray() {
        requireArray();
        byte[] arr = new byte[size()];
        for (int i = 0, l = size(); i < l; i++) {
            try {
                arr[i] = get(i).asByte();
            } catch (IncorrectTypeException exc) {
                throw Internal.withIndex(exc, i);
            }
        }
        return arr;
    }

    @Override
    public short[] asShortArray() {
        requireArray();
        short[] arr = new short[size()];
        for (int i = 0, l = size(); i < l; i++) {
            try {
                arr[i] = get(i).asShort();
            } catch (IncorrectTypeException exc) {
                throw Internal.withIndex(exc, i);
            }
        }
        return arr;
    }

    @Override
    public int[] asIntArray() {
        requireArray();
        int[] arr = new int[size()];
        for (int i = 0, l = size(); i < l; i++) {
            try {
                arr[i] = get(i).asInt();
            } catch (IncorrectTypeException exc) {
                throw Internal.withIndex(exc, i);
            }
        }
        return arr;
    }

    @Override
    public long[] asLongArray() {
        requireArray();
        long[] arr = new long[size()];
        for (int i = 0, l = size(); i < l; i++) {
            try {
                arr[i] = get(i).asLong();
            } catch (IncorrectTypeException exc) {
                throw Internal.withIndex(exc, i);
            }
        }
        return arr;
    }

    @Override
    public float[] asFloatArray() {
        requireArray();
        float[] arr = new float[size()];
        for (int i = 0, l = size(); i < l; i++) {
            try {
                arr[i] = get(i).asFloat();
            } catch (IncorrectTypeException exc) {
                throw Internal.withIndex(exc, i);
            }
        }
        return arr;
    }

    @Override
    public double[] asDoubleArray() {
        requireArray();
        double[] arr = new double[size()];
        for (int i = 0, l = size(); i < l; i++) {
            try {
                arr[i] = get(i).asDouble();
            } catch (IncorrectTypeException exc) {
                throw Internal.withIndex(exc, i);
            }
        }
        return arr;
    }

    @Override
    public BigInteger[] asBigIntegerArray() {
        requireArray();
        BigInteger[] arr = new BigInteger[size()];
        for (int i = 0, l = size(); i < l; i++) {
            try {
                arr[i] = get(i).asBigInteger();
            } catch (IncorrectTypeException exc) {
                throw Internal.withIndex(exc, i);
            }
        }
        return arr;
    }

    @Override
    public BigDecimal[] asBigDecimalArray() {
        requireArray();
        BigDecimal[] arr = new BigDecimal[size()];
        for (int i = 0, l = size(); i < l; i++) {
            try {
                arr[i] = get(i).asBigDecimal();
            } catch (IncorrectTypeException exc) {
                throw Internal.withIndex(exc, i);
            }
        }
        return arr;
    }

    @Override
    public Number[] asNumberArray() {
        requireArray();
        Number[] arr = new Number[size()];
        for (int i = 0, l = size(); i < l; i++) {
            try {
                arr[i] = get(i).asNumber();
            } catch (IncorrectTypeException exc) {
                throw Internal.withIndex(exc, i);
            }
        }
        return arr;
    }

    @Override
    public boolean[] asBooleanArray() {
        requireArray();
        boolean[] arr = new boolean[size()];
        for (int i = 0, l = size(); i < l; i++) {
            try {
                arr[i] = get(i).asBoolean();
            } catch (IncorrectTypeException exc) {
                throw Internal.withIndex(exc, i);
            }
        }
        return arr;
    }

    @Override
    public Temporal[] asTemporalArray() {
        requireArray();
        Temporal[] arr = new Temporal[size()];
        for (int i = 0, l = size(); i < l; i++) {
            try {
                arr[i] = get(i).asTemporal();
            } catch (IncorrectTypeException exc) {
                throw Internal.withIndex(exc, i);
            }
        }
        return arr;
    }

    @Override
    public OffsetDateTime[] asOffsetDateTimeArray() {
        requireArray();
        OffsetDateTime[] arr = new OffsetDateTime[size()];
        for (int i = 0, l = size(); i < l; i++) {
            try {
                arr[i] = get(i).asOffsetDateTime();
            } catch (IncorrectTypeException exc) {
                throw Internal.withIndex(exc, i);
            }
        }
        return arr;
    }

    @Override
    public LocalDateTime[] asLocalDateTimeArray() {
        requireArray();
        LocalDateTime[] arr = new LocalDateTime[size()];
        for (int i = 0, l = size(); i < l; i++) {
            try {
                arr[i] = get(i).asLocalDateTime();
            } catch (IncorrectTypeException exc) {
                throw Internal.withIndex(exc, i);
            }
        }
        return arr;
    }

    @Override
    public LocalDate[] asLocalDateArray() {
        requireArray();
        LocalDate[] arr = new LocalDate[size()];
        for (int i = 0, l = size(); i < l; i++) {
            try {
                arr[i] = get(i).asLocalDate();
            } catch (IncorrectTypeException exc) {
                throw Internal.withIndex(exc, i);
            }
        }
        return arr;
    }

    @Override
    public LocalTime[] asLocalTimeArray() {
        requireArray();
        LocalTime[] arr = new LocalTime[size()];
        for (int i = 0, l = size(); i < l; i++) {
            try {
                arr[i] = get(i).asLocalTime();
            } catch (IncorrectTypeException exc) {
                throw Internal.withIndex(exc, i);
            }
        }
        return arr;
    }

    @Override
    public String[] showArray(int fixedLength) {
        requireArray();
        requireSize(fixedLength);
        return showArray();
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
    public Temporal[] asTemporalArray(int fixedLength) {
        requireArray();
        requireSize(fixedLength);
        return asTemporalArray();
    }

    @Override
    public OffsetDateTime[] asOffsetDateTimeArray(int fixedLength) {
        requireArray();
        requireSize(fixedLength);
        return asOffsetDateTimeArray();
    }

    @Override
    public LocalDateTime[] asLocalDateTimeArray(int fixedLength) {
        requireArray();
        requireSize(fixedLength);
        return asLocalDateTimeArray();
    }

    @Override
    public LocalDate[] asLocalDateArray(int fixedLength) {
        requireArray();
        requireSize(fixedLength);
        return asLocalDateArray();
    }

    @Override
    public LocalTime[] asLocalTimeArray(int fixedLength) {
        requireArray();
        requireSize(fixedLength);
        return asLocalTimeArray();
    }

    @Override
    public List<JsonNode> asList() {
        requireArray();
        List<JsonNode> nodes = new ArrayList<>();
        forEach(nodes::add);
        return nodes;
    }

    @Override
    public Map<String, JsonNode> asMap() {
        requireArray();
        Map<String, JsonNode> nodes = new LinkedHashMap<>();
        forEachEntry(nodes::put);
        return nodes;
    }

    @Override
    public JsonNode requireSize(int size) {
        if (size() != size)
            throw new IncorrectSizeException(size(), size);
        return this;
    }

    @Override
    public JsonNode requireMinSize(int size) {
        if (size() < size)
            throw new IncorrectSizeException(size(), size, Integer.MAX_VALUE);
        return this;
    }

    @Override
    public JsonNode requireMaxSize(int size) {
        if (size() > size)
            throw new IncorrectSizeException(size(), Integer.MIN_VALUE, size);
        return this;
    }

    @Override
    public JsonNode requireSize(int minSize, int maxSize) {
        if (size() < minSize || size() > maxSize)
            throw new IncorrectSizeException(size(), minSize, maxSize);
        return this;
    }

    @Override
    public JsonNode wrap() {
        return JsonNode.array(this);
    }

    @Override
    public JsonNode wrap(String key) {
        return JsonNode.object(key, this);
    }

    @Override
    public JsonNode append(JsonNode other) {
        for (JsonNode n : other) {
            add(n);
        }
        return this;
    }

    @Override
    public JsonNode prepend(JsonNode other) {
        int i = 0;
        for (JsonNode n : other) {
            insert(i++, n);
        }
        return this;
    }



    @Override
    public JsonNode set(int index, String value) {
        requireArray();
        return set(index, JsonNode.string(value));
    }

    @Override
    public JsonNode set(int index, Number value) {
        requireArray();
        return set(index, JsonNode.number(value));
    }

    @Override
    public JsonNode set(int index, Boolean value) {
        requireArray();
        return set(index, JsonNode.bool(value));
    }

    @Override
    public JsonNode set(int index, OffsetDateTime value) {
        requireArray();
        return set(index, JsonNode.offsetDateTime(value));
    }

    @Override
    public JsonNode set(int index, LocalDateTime value) {
        requireArray();
        return set(index, JsonNode.localDateTime(value));
    }

    @Override
    public JsonNode set(int index, LocalDate value) {
        requireArray();
        return set(index, JsonNode.localDate(value));
    }

    @Override
    public JsonNode set(int index, LocalTime value) {
        requireArray();
        return set(index, JsonNode.localTime(value));
    }

    @Override
    public JsonNode add(String value) {
        requireArray();
        return add(JsonNode.string(value));
    }

    @Override
    public JsonNode add(Number value) {
        requireArray();
        return add(JsonNode.number(value));
    }

    @Override
    public JsonNode add(Boolean value) {
        requireArray();
        return add(JsonNode.bool(value));
    }

    @Override
    public JsonNode add(OffsetDateTime value) {
        requireArray();
        return add(JsonNode.offsetDateTime(value));
    }

    @Override
    public JsonNode add(LocalDateTime value) {
        requireArray();
        return add(JsonNode.localDateTime(value));
    }

    @Override
    public JsonNode add(LocalDate value) {
        requireArray();
        return add(JsonNode.localDate(value));
    }

    @Override
    public JsonNode add(LocalTime value) {
        requireArray();
        return add(JsonNode.localTime(value));
    }

    @Override
    public JsonNode insert(int index, String value) {
        requireArray();
        return insert(index, JsonNode.string(value));
    }

    @Override
    public JsonNode insert(int index, Number value) {
        requireArray();
        return insert(index, JsonNode.number(value));
    }

    @Override
    public JsonNode insert(int index, Boolean value) {
        requireArray();
        return insert(index, JsonNode.bool(value));
    }

    @Override
    public JsonNode insert(int index, OffsetDateTime value) {
        requireArray();
        return insert(index, JsonNode.offsetDateTime(value));
    }

    @Override
    public JsonNode insert(int index, LocalDateTime value) {
        requireArray();
        return insert(index, JsonNode.localDateTime(value));
    }

    @Override
    public JsonNode insert(int index, LocalDate value) {
        requireArray();
        return insert(index, JsonNode.localDate(value));
    }

    @Override
    public JsonNode insert(int index, LocalTime value) {
        requireArray();
        return insert(index, JsonNode.localTime(value));
    }

    @Override
    public JsonNode set(String key, String value) {
        requireObject();
        return set(key, JsonNode.string(value));
    }

    @Override
    public JsonNode set(String key, Number value) {
        requireObject();
        return set(key, JsonNode.number(value));
    }

    @Override
    public JsonNode set(String key, Boolean value) {
        requireObject();
        return set(key, JsonNode.bool(value));
    }

    @Override
    public JsonNode set(String key, OffsetDateTime value) {
        requireObject();
        return set(key, JsonNode.offsetDateTime(value));
    }

    @Override
    public JsonNode set(String key, LocalDateTime value) {
        requireObject();
        return set(key, JsonNode.localDateTime(value));
    }

    @Override
    public JsonNode set(String key, LocalDate value) {
        requireObject();
        return set(key, JsonNode.localDate(value));
    }

    @Override
    public JsonNode set(String key, LocalTime value) {
        requireObject();
        return set(key, JsonNode.localTime(value));
    }


    @Override
    public JsonNode showNode() {
        return JsonNode.string(show());
    }
}
