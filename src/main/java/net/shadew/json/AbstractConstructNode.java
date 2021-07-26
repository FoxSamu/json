package net.shadew.json;

import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class AbstractConstructNode extends AbstractJsonNode {
    protected AbstractConstructNode(JsonType type) {
        super(type);
    }

    @Override
    public String asString() {
        throw new IncorrectTypeException(type(), JsonType.STRING);
    }

    @Override
    public byte asByte() {
        throw new IncorrectTypeException(type(), JsonType.NUMBER);
    }

    @Override
    public short asShort() {
        throw new IncorrectTypeException(type(), JsonType.NUMBER);
    }

    @Override
    public int asInt() {
        throw new IncorrectTypeException(type(), JsonType.NUMBER);
    }

    @Override
    public long asLong() {
        throw new IncorrectTypeException(type(), JsonType.NUMBER);
    }

    @Override
    public float asFloat() {
        throw new IncorrectTypeException(type(), JsonType.NUMBER);
    }

    @Override
    public double asDouble() {
        throw new IncorrectTypeException(type(), JsonType.NUMBER);
    }

    @Override
    public BigInteger asBigInteger() {
        throw new IncorrectTypeException(type(), JsonType.NUMBER);
    }

    @Override
    public BigDecimal asBigDecimal() {
        throw new IncorrectTypeException(type(), JsonType.NUMBER);
    }

    @Override
    public boolean asBoolean() {
        throw new IncorrectTypeException(type(), JsonType.BOOLEAN);
    }
}
