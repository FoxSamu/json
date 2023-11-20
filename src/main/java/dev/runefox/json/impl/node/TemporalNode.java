package dev.runefox.json.impl.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.Temporal;
import java.util.Objects;

public abstract class TemporalNode<T extends Temporal> extends PrimitiveNode {
    private final T temporal;

    public TemporalNode(T temporal) {
        this.temporal = temporal;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public boolean isBoolean() {
        return false;
    }

    @Override
    public String show() {
        return temporal.toString();
    }

    @Override
    public String asString() {
        throw expectedType("STRING");
    }

    @Override
    public byte asByte() {
        throw expectedType("NUMBER");
    }

    @Override
    public short asShort() {
        throw expectedType("NUMBER");
    }

    @Override
    public int asInt() {
        throw expectedType("NUMBER");
    }

    @Override
    public long asLong() {
        throw expectedType("NUMBER");
    }

    @Override
    public float asFloat() {
        throw expectedType("NUMBER");
    }

    @Override
    public double asDouble() {
        throw expectedType("NUMBER");
    }

    @Override
    public BigInteger asBigInteger() {
        throw expectedType("NUMBER");
    }

    @Override
    public BigDecimal asBigDecimal() {
        throw expectedType("NUMBER");
    }

    @Override
    public Number asNumber() {
        throw expectedType("NUMBER");
    }

    @Override
    public boolean asBoolean() {
        throw expectedType("BOOLEAN");
    }

    @Override
    public T asTemporal() {
        return temporal;
    }

    @Override
    public String toString() {
        return StringNode.quote(show());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemporalNode<?> that = (TemporalNode<?>) o;
        return Objects.equals(temporal, that.temporal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(temporal);
    }
}
