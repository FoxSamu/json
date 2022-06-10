package net.shadew.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Consumer;

public abstract class AbstractConstructNode extends AbstractJsonNode {
    protected AbstractConstructNode(JsonType type) {
        super(type);
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public boolean isConstruct() {
        return true;
    }

    @Override
    public JsonNode requirePrimitive() {
        throw new IncorrectTypeException(type(), JsonType.PRIMITIVES);
    }

    @Override
    public JsonNode requireNotPrimitive() {
        return this;
    }

    @Override
    public JsonNode requireConstruct() {
        return this;
    }

    @Override
    public JsonNode requireNotConstruct() {
        throw new IncorrectTypeException(type(), JsonType.NOT_CONSTRUCTS);
    }

    @Override
    public JsonNode ifConstruct(Consumer<JsonNode> action) {
        action.accept(this);
        return this;
    }

    @Override
    public String asExactString() {
        throw new IncorrectTypeException(type(), JsonType.STRING);
    }

    @Override
    public String asString() {
        throw new IncorrectTypeException(type(), JsonType.NOT_CONSTRUCTS);
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
    public BigDecimal asNumber() {
        throw new IncorrectTypeException(type(), JsonType.NUMBER);
    }

    @Override
    public boolean asBoolean() {
        throw new IncorrectTypeException(type(), JsonType.BOOLEAN);
    }
}
