package dev.runefox.json.impl.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.temporal.Temporal;
import java.util.Objects;

public final class StringNode extends PrimitiveNode {
    private final String string;

    public StringNode(String string) {
        this.string = string;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean isString() {
        return true;
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
    protected String describeType() {
        return "STRING";
    }

    @Override
    public String asString() {
        return string;
    }

    @Override
    public String show() {
        return string;
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
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        StringNode other = (StringNode) o;
        return string.equals(other.string);
    }

    @Override
    public int hashCode() {
        return Objects.hash(string);
    }

    @Override
    public String toString() {
        return quote(string);
    }

    public static void quote(String string, StringBuilder builder, char quote) {
        builder.append(quote);
        for (int i = 0, l = string.length(); i < l; i++) {
            char c = string.charAt(i);
            if (c == quote || c == '\\')
                builder.append("\\");

            if (c < 0x20 || c > 0x7F) {
                if (c == '\n')
                    builder.append("\\n");
                else if (c == '\r')
                    builder.append("\\r");
                else if (c == '\t')
                    builder.append("\\t");
                else if (c == '\f')
                    builder.append("\\f");
                else if (c == '\b')
                    builder.append("\\b");
                else {
                    builder.append(String.format("\\u%04X", (int) c));
                }
            } else {
                builder.append(c);
            }
        }
        builder.append(quote);
    }

    public static String quote(String string) {
        StringBuilder builder = new StringBuilder();
        quote(string, builder, '"');
        return builder.toString();
    }
}
