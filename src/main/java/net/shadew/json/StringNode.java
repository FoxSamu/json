package net.shadew.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

final class StringNode extends AbstractPrimitiveNode {
    private final String string;

    StringNode(String string) {
        super(JsonType.STRING);
        this.string = string;
    }

    @Override
    public String asString() {
        return string;
    }

    @Override
    public byte asByte() {
        throw new IncorrectTypeException(JsonType.STRING, JsonType.NUMBER);
    }

    @Override
    public short asShort() {
        throw new IncorrectTypeException(JsonType.STRING, JsonType.NUMBER);
    }

    @Override
    public int asInt() {
        throw new IncorrectTypeException(JsonType.STRING, JsonType.NUMBER);
    }

    @Override
    public long asLong() {
        throw new IncorrectTypeException(JsonType.STRING, JsonType.NUMBER);
    }

    @Override
    public float asFloat() {
        throw new IncorrectTypeException(JsonType.STRING, JsonType.NUMBER);
    }

    @Override
    public double asDouble() {
        throw new IncorrectTypeException(JsonType.STRING, JsonType.NUMBER);
    }

    @Override
    public BigInteger asBigInteger() {
        throw new IncorrectTypeException(JsonType.STRING, JsonType.NUMBER);
    }

    @Override
    public BigDecimal asBigDecimal() {
        throw new IncorrectTypeException(JsonType.STRING, JsonType.NUMBER);
    }

    @Override
    public boolean asBoolean() {
        throw new IncorrectTypeException(JsonType.STRING, JsonType.BOOLEAN);
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
