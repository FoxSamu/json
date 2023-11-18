package dev.runefox.json.impl.node;

import dev.runefox.json.IncorrectTypeException;
import dev.runefox.json.JsonNode;
import dev.runefox.json.NodeType;
import dev.runefox.json.impl.Internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Consumer;

public abstract class AbstractConstructNode extends AbstractJsonNode {
    protected AbstractConstructNode(NodeType type) {
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
        throw new IncorrectTypeException(type(), Internal.PRIMITIVES);
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
        throw new IncorrectTypeException(type(), Internal.NOT_CONSTRUCTS);
    }

    @Override
    public JsonNode ifConstruct(Consumer<JsonNode> action) {
        action.accept(this);
        return this;
    }

    @Override
    public String asExactString() {
        throw new IncorrectTypeException(type(), NodeType.STRING);
    }

    @Override
    public String asString() {
        throw new IncorrectTypeException(type(), Internal.NOT_CONSTRUCTS);
    }

    @Override
    public byte asByte() {
        throw new IncorrectTypeException(type(), NodeType.NUMBER);
    }

    @Override
    public short asShort() {
        throw new IncorrectTypeException(type(), NodeType.NUMBER);
    }

    @Override
    public int asInt() {
        throw new IncorrectTypeException(type(), NodeType.NUMBER);
    }

    @Override
    public long asLong() {
        throw new IncorrectTypeException(type(), NodeType.NUMBER);
    }

    @Override
    public float asFloat() {
        throw new IncorrectTypeException(type(), NodeType.NUMBER);
    }

    @Override
    public double asDouble() {
        throw new IncorrectTypeException(type(), NodeType.NUMBER);
    }

    @Override
    public BigInteger asBigInteger() {
        throw new IncorrectTypeException(type(), NodeType.NUMBER);
    }

    @Override
    public BigDecimal asBigDecimal() {
        throw new IncorrectTypeException(type(), NodeType.NUMBER);
    }

    @Override
    public BigDecimal asNumber() {
        throw new IncorrectTypeException(type(), NodeType.NUMBER);
    }

    @Override
    public boolean asBoolean() {
        throw new IncorrectTypeException(type(), NodeType.BOOLEAN);
    }
}
