package dev.runefox.json.impl.node;

import dev.runefox.json.IncorrectTypeException;
import dev.runefox.json.JsonNode;
import dev.runefox.json.NodeType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.function.BiConsumer;

public final class StringNode extends AbstractPrimitiveNode {
    private final String string;

    public StringNode(String string) {
        super(NodeType.STRING);
        this.string = string;
    }

    @Override
    public JsonNode ifString(BiConsumer<JsonNode, String> action) {
        action.accept(this, string);
        return this;
    }

    @Override
    public String asExactString() {
        return string;
    }

    @Override
    public String asString() {
        return string;
    }

    @Override
    public byte asByte() {
        throw new IncorrectTypeException(NodeType.STRING, NodeType.NUMBER);
    }

    @Override
    public short asShort() {
        throw new IncorrectTypeException(NodeType.STRING, NodeType.NUMBER);
    }

    @Override
    public int asInt() {
        throw new IncorrectTypeException(NodeType.STRING, NodeType.NUMBER);
    }

    @Override
    public long asLong() {
        throw new IncorrectTypeException(NodeType.STRING, NodeType.NUMBER);
    }

    @Override
    public float asFloat() {
        throw new IncorrectTypeException(NodeType.STRING, NodeType.NUMBER);
    }

    @Override
    public double asDouble() {
        throw new IncorrectTypeException(NodeType.STRING, NodeType.NUMBER);
    }

    @Override
    public BigInteger asBigInteger() {
        throw new IncorrectTypeException(NodeType.STRING, NodeType.NUMBER);
    }

    @Override
    public BigDecimal asBigDecimal() {
        throw new IncorrectTypeException(NodeType.STRING, NodeType.NUMBER);
    }

    @Override
    public Number asNumber() {
        throw new IncorrectTypeException(NodeType.STRING, NodeType.NUMBER);
    }

    @Override
    public boolean asBoolean() {
        throw new IncorrectTypeException(NodeType.STRING, NodeType.BOOLEAN);
    }

    @Override
    public int length() {
        return string.length();
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

    static String quote(String string) {
        StringBuilder builder = new StringBuilder();
        quote(string, builder, '"');
        return builder.toString();
    }
}
