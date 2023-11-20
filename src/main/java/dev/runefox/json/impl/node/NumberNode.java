package dev.runefox.json.impl.node;

import dev.runefox.json.impl.KotlinNumberWrapper;
import dev.runefox.json.impl.LazyParseNumber;
import dev.runefox.json.impl.LazyParseRadix;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.temporal.Temporal;
import java.util.Objects;

public final class NumberNode extends PrimitiveNode {
    private final Number number;
    private BigInteger bigInteger;
    private BigDecimal bigDecimal;
    private String string;

    public NumberNode(Number number) {
        this.number = number;
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
        return true;
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
        return number.toString();
    }

    @Override
    public String asString() {
        throw expectedType("STRING");
    }

    @Override
    public byte asByte() {
        return number.byteValue();
    }

    @Override
    public short asShort() {
        return number.shortValue();
    }

    @Override
    public int asInt() {
        return number.intValue();
    }

    @Override
    public long asLong() {
        return number.longValue();
    }

    @Override
    public float asFloat() {
        return number.floatValue();
    }

    @Override
    public double asDouble() {
        return number.doubleValue();
    }

    @Override
    public BigInteger asBigInteger() {
        if (bigInteger != null)
            return bigInteger;

        if (number instanceof BigInteger bi)
            return bigInteger = bi;

        if (number instanceof BigDecimal bd)
            return bigInteger = bd.toBigInteger();

        if (number instanceof LazyParseNumber lpn)
            return bigInteger = lpn.bigIntegerValue();

        if (number instanceof LazyParseRadix lpr)
            return bigInteger = lpr.bigIntegerValue();

        if (number instanceof KotlinNumberWrapper knw)
            return bigInteger = knw.toBigInteger();

        return bigInteger = asBigDecimal().toBigInteger();
    }

    @Override
    public BigDecimal asBigDecimal() {
        if (bigDecimal != null)
            return bigDecimal;

        if (number instanceof BigInteger bi)
            return bigDecimal = new BigDecimal(bi);

        if (number instanceof BigDecimal bd)
            return bigDecimal = bd;

        if (number instanceof LazyParseNumber lpn)
            return bigDecimal = lpn.bigDecimalValue();

        if (number instanceof LazyParseRadix lpr)
            return bigDecimal = lpr.bigDecimalValue();

        if (number instanceof KotlinNumberWrapper knw)
            return bigDecimal = knw.toBigDecimal();

        return bigDecimal = BigDecimal.valueOf(asDouble());
    }

    @Override
    public Number asNumber() {
        return number;
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

    // We use BigDecimal to resemble this number so numbers of different types are equal
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        NumberNode other = (NumberNode) o;
        return asBigDecimal().equals(other.asBigDecimal());
    }

    @Override
    public int hashCode() {
        return Objects.hash(asBigDecimal());
    }

    @Override
    public String toString() {
        if (string != null)
            return string;

        if (number instanceof KotlinNumberWrapper knw) {
            return string = knw.represent();
        }

        if (number instanceof LazyParseNumber lpn) {
            return string = lpn.toJsonValidString();
        }

        if (number instanceof LazyParseRadix lpr) {
            return string = lpr.toJsonValidString();
        }

        BigDecimal decimal = asBigDecimal();
        try {
            BigInteger integer = decimal.toBigIntegerExact();
            return string = integer.toString();
        } catch (ArithmeticException exc) {
            return string = decimal.toString();
        }
    }

    @Override
    protected String describeType() {
        return "NUMBER";
    }
}
