package net.shadew.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Consumer;

final class NullNode extends AbstractPrimitiveNode {
    NullNode() {
        super(JsonType.NULL);
    }

    @Override
    public JsonNode ifNull(Consumer<JsonNode> action) {
        action.accept(this);
        return this;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public JsonNode requirePrimitive() {
        throw new IncorrectTypeException(JsonType.NULL, JsonType.PRIMITIVES);
    }

    @Override
    public String asExactString() {
        throw new IncorrectTypeException(JsonType.NULL, JsonType.STRING);
    }

    @Override
    public String asString() {
        return "null";
    }

    @Override
    public byte asByte() {
        throw new IncorrectTypeException(JsonType.NULL, JsonType.NUMBER);
    }

    @Override
    public short asShort() {
        throw new IncorrectTypeException(JsonType.NULL, JsonType.NUMBER);
    }

    @Override
    public int asInt() {
        throw new IncorrectTypeException(JsonType.NULL, JsonType.NUMBER);
    }

    @Override
    public long asLong() {
        throw new IncorrectTypeException(JsonType.NULL, JsonType.NUMBER);
    }

    @Override
    public float asFloat() {
        throw new IncorrectTypeException(JsonType.NULL, JsonType.NUMBER);
    }

    @Override
    public double asDouble() {
        throw new IncorrectTypeException(JsonType.NULL, JsonType.NUMBER);
    }

    @Override
    public BigInteger asBigInteger() {
        throw new IncorrectTypeException(JsonType.NULL, JsonType.NUMBER);
    }

    @Override
    public BigDecimal asBigDecimal() {
        throw new IncorrectTypeException(JsonType.NULL, JsonType.NUMBER);
    }

    @Override
    public BigDecimal asNumber() {
        throw new IncorrectTypeException(JsonType.NULL, JsonType.NUMBER);
    }

    @Override
    public boolean asBoolean() {
        throw new IncorrectTypeException(JsonType.NULL, JsonType.BOOLEAN);
    }

    @Override
    public int hashCode() {
        return 42;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        return obj.getClass() == getClass();
    }

    @Override
    public String toString() {
        return "null";
    }
}
