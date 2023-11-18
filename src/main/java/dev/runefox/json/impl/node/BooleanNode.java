package dev.runefox.json.impl.node;

import dev.runefox.json.IncorrectTypeException;
import dev.runefox.json.JsonNode;
import dev.runefox.json.NodeType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.function.BiConsumer;

public final class BooleanNode extends AbstractPrimitiveNode {
    private final boolean bool;
    private final String string;

    public BooleanNode(boolean bool) {
        super(NodeType.BOOLEAN);
        this.bool = bool;
        this.string = bool ? "true" : "false";
    }

    @Override
    public JsonNode ifBoolean(BiConsumer<JsonNode, Boolean> action) {
        action.accept(this, bool);
        return this;
    }

    @Override
    public String asExactString() {
        throw new IncorrectTypeException(NodeType.BOOLEAN, NodeType.STRING);
    }

    @Override
    public String asString() {
        return string;
    }

    @Override
    public byte asByte() {
        throw new IncorrectTypeException(NodeType.BOOLEAN, NodeType.NUMBER);
    }

    @Override
    public short asShort() {
        throw new IncorrectTypeException(NodeType.BOOLEAN, NodeType.NUMBER);
    }

    @Override
    public int asInt() {
        throw new IncorrectTypeException(NodeType.BOOLEAN, NodeType.NUMBER);
    }

    @Override
    public long asLong() {
        throw new IncorrectTypeException(NodeType.BOOLEAN, NodeType.NUMBER);
    }

    @Override
    public float asFloat() {
        throw new IncorrectTypeException(NodeType.BOOLEAN, NodeType.NUMBER);
    }

    @Override
    public double asDouble() {
        throw new IncorrectTypeException(NodeType.BOOLEAN, NodeType.NUMBER);
    }

    @Override
    public BigInteger asBigInteger() {
        throw new IncorrectTypeException(NodeType.BOOLEAN, NodeType.NUMBER);
    }

    @Override
    public BigDecimal asBigDecimal() {
        throw new IncorrectTypeException(NodeType.BOOLEAN, NodeType.NUMBER);
    }

    @Override
    public BigDecimal asNumber() {
        throw new IncorrectTypeException(NodeType.BOOLEAN, NodeType.NUMBER);
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

    @Override
    public String toString() {
        return string;
    }
}
