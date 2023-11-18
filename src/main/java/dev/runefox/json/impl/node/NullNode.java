package dev.runefox.json.impl.node;

import dev.runefox.json.IncorrectTypeException;
import dev.runefox.json.JsonNode;
import dev.runefox.json.NodeType;
import dev.runefox.json.impl.Internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Consumer;

public final class NullNode extends AbstractPrimitiveNode {
    public NullNode() {
        super(NodeType.NULL);
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
        throw new IncorrectTypeException(NodeType.NULL, Internal.PRIMITIVES);
    }

    @Override
    public String asExactString() {
        throw new IncorrectTypeException(NodeType.NULL, NodeType.STRING);
    }

    @Override
    public String asString() {
        return "null";
    }

    @Override
    public byte asByte() {
        throw new IncorrectTypeException(NodeType.NULL, NodeType.NUMBER);
    }

    @Override
    public short asShort() {
        throw new IncorrectTypeException(NodeType.NULL, NodeType.NUMBER);
    }

    @Override
    public int asInt() {
        throw new IncorrectTypeException(NodeType.NULL, NodeType.NUMBER);
    }

    @Override
    public long asLong() {
        throw new IncorrectTypeException(NodeType.NULL, NodeType.NUMBER);
    }

    @Override
    public float asFloat() {
        throw new IncorrectTypeException(NodeType.NULL, NodeType.NUMBER);
    }

    @Override
    public double asDouble() {
        throw new IncorrectTypeException(NodeType.NULL, NodeType.NUMBER);
    }

    @Override
    public BigInteger asBigInteger() {
        throw new IncorrectTypeException(NodeType.NULL, NodeType.NUMBER);
    }

    @Override
    public BigDecimal asBigDecimal() {
        throw new IncorrectTypeException(NodeType.NULL, NodeType.NUMBER);
    }

    @Override
    public BigDecimal asNumber() {
        throw new IncorrectTypeException(NodeType.NULL, NodeType.NUMBER);
    }

    @Override
    public boolean asBoolean() {
        throw new IncorrectTypeException(NodeType.NULL, NodeType.BOOLEAN);
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
