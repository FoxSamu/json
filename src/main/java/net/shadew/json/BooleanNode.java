package net.shadew.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

final class BooleanNode extends AbstractPrimitiveNode {
    private final boolean bool;

    BooleanNode(boolean bool) {
        super(JsonType.BOOLEAN);
        this.bool = bool;
    }

    @Override
    public String asString() {
        throw new IncorrectTypeException(JsonType.BOOLEAN, JsonType.STRING);
    }

    @Override
    public byte asByte() {
        throw new IncorrectTypeException(JsonType.BOOLEAN, JsonType.NUMBER);
    }

    @Override
    public short asShort() {
        throw new IncorrectTypeException(JsonType.BOOLEAN, JsonType.NUMBER);
    }

    @Override
    public int asInt() {
        throw new IncorrectTypeException(JsonType.BOOLEAN, JsonType.NUMBER);
    }

    @Override
    public long asLong() {
        throw new IncorrectTypeException(JsonType.BOOLEAN, JsonType.NUMBER);
    }

    @Override
    public float asFloat() {
        throw new IncorrectTypeException(JsonType.BOOLEAN, JsonType.NUMBER);
    }

    @Override
    public double asDouble() {
        throw new IncorrectTypeException(JsonType.BOOLEAN, JsonType.NUMBER);
    }

    @Override
    public BigInteger asBigInteger() {
        throw new IncorrectTypeException(JsonType.BOOLEAN, JsonType.NUMBER);
    }

    @Override
    public BigDecimal asBigDecimal() {
        throw new IncorrectTypeException(JsonType.BOOLEAN, JsonType.NUMBER);
    }

    @Override
    public boolean asBoolean() {
        return bool;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        BooleanNode other = (BooleanNode) o;
        return bool == other.bool;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bool);
    }
}
