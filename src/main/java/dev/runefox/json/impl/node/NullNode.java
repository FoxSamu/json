package dev.runefox.json.impl.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.temporal.Temporal;

public final class NullNode extends PrimitiveNode {
    public static final NullNode INSTANCE = new NullNode();

    private NullNode() {
    }

    @Override
    public boolean isNull() {
        return true;
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
    public boolean isOffsetDateTime() {
        return false;
    }

    @Override
    public boolean isLocalDateTime() {
        return false;
    }

    @Override
    public boolean isLocalDate() {
        return false;
    }

    @Override
    public boolean isLocalTime() {
        return false;
    }

    @Override
    public String show() {
        return "null";
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
    public Temporal asTemporal() {
        throw expectedType("TEMPORAL");
    }

    @Override
    public OffsetDateTime asOffsetDateTime() {
        throw expectedType("OFFSET_DATE_TIME");
    }

    @Override
    public LocalDateTime asLocalDateTime() {
        throw expectedType("LOCAL_DATE_TIME");
    }

    @Override
    public LocalDate asLocalDate() {
        throw expectedType("LOCAL_DATE");
    }

    @Override
    public LocalTime asLocalTime() {
        throw expectedType("LOCAL_TIME");
    }

    @Override
    protected String describeType() {
        return "NULL";
    }

    @Override
    public String toString() {
        return show();
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == getClass();
    }

    @Override
    public int hashCode() {
        return 621;
    }
}
