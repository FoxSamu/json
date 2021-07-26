package net.shadew.json;

import java.math.BigDecimal;
import java.math.BigInteger;

final class NullNode extends AbstractPrimitiveNode {
    NullNode() {
        super(JsonType.NULL);
    }

    @Override
    public String asString() {
        throw new IncorrectTypeException(JsonType.NULL, JsonType.STRING);
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
