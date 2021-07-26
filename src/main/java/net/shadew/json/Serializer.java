package net.shadew.json;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Serializer {
    private static final ThreadLocal<Serializer> SERIALIZER_INSTANCE = ThreadLocal.withInitial(Serializer::new);
    private Appendable output;
    private FormattingConfig config;
    private int nextSpacing;
    private final Set<String> validIds = new HashSet<>();
    private final Map<String, String> stringToJsonCache = new HashMap<>();
    private final StringBuilder builder = new StringBuilder();
    private char quote;

    private int indent = 0;

    Serializer() {

    }

    void reset(Appendable output, FormattingConfig config) {
        builder.setLength(0);
        this.output = output;
        this.config = config;
        this.quote = config.useSingleQuoteStrings() && config.json5() ? '\'' : '"';
        this.indent = 0;
        this.nextSpacing = 0;
    }

    private void addSpacing(int count) throws IOException {
        if (nextSpacing > count)
            count = nextSpacing;
        nextSpacing = 0;
        addSpaces(count);
    }

    private void addSpaces(int count) throws IOException {
        output.append(" ".repeat(count));
    }

    private void addTabs(int count) throws IOException {
        output.append("\t".repeat(count));
    }

    private void addNewline() throws IOException {
        output.append(config.lineSeparator().toString());
        addIndent(indent);
    }

    private void addIndent(int count) throws IOException {
        if (config.tabIndent()) {
            addTabs(config.indent() * count);
        } else {
            addSpaces(config.indent() * count);
        }
        nextSpacing = 0;
    }

    private String stringToJson(String str) {
        if (stringToJsonCache.containsKey(str)) {
            return stringToJsonCache.get(str);
        }
        builder.setLength(0);
        StringNode.quote(str, builder, quote);
        String converted = builder.toString();
        stringToJsonCache.put(str, converted);
        return converted;
    }

    private String keyToJson(String str) {
        if (config.json5() && config.useIdentifierKeys()) {
            if (validIds.contains(str))
                return str;
            if (CharUtil.isIdentifierValid(str)) {
                validIds.add(str);
                return str;
            }
        }
        return stringToJson(str);
    }

    private int getObjectKeyAlignmentLength(JsonNode object) {
        if (!config.alignObjectValues()) return 0;

        int len = 0;
        for (String key : object.keys()) {
            String jsonKey = keyToJson(key);
            len = Math.max(len, jsonKey.length());
        }
        return len;
    }

    private void writeString(String str) throws IOException {
        addSpacing(0);
        output.append(stringToJson(str));
    }

    private void writeNumber(BigDecimal decimal) throws IOException {
        addSpacing(0);
        String str = decimal.toString();
        if (config.ensurePointInNumbers()) {
            if (!str.contains(".") && !str.contains("e"))
                output.append(str).append(".0");
            else
                output.append(str);
        } else {
            if (str.contains("e")) {
                output.append(str);
            } else {
                try {
                    BigInteger integer = decimal.toBigIntegerExact();
                    output.append(integer.toString());
                } catch (ArithmeticException exc) {
                    output.append(decimal.toString());
                }
            }
        }
    }

    private void writeBoolean(boolean bool) throws IOException {
        addSpacing(0);
        output.append(bool ? "true" : "false");
    }

    private void writeNull() throws IOException {
        addSpacing(0);
        output.append("null");
    }

    private void writeComma() throws IOException {
        addSpacing(config.spacesBeforeComma());
        output.append(",");
        nextSpacing = config.spacesAfterComma();
    }

    private void writeColon() throws IOException {
        addSpacing(config.spacesBeforeColon());
        output.append(":");
        nextSpacing = config.spacesAfterColon();
    }

    private void writeArray(JsonNode array) throws IOException {
        if (array.size() == 0) {
            addSpacing(config.spacesAroundArray());
            output.append('[');
            addSpacing(config.spacesWithinEmptyArray());
            output.append(']');
            nextSpacing = config.spacesAroundArray();
        } else {
            addSpacing(config.spacesAroundArray());
            output.append('[');
            nextSpacing = config.spacesWithinArray();

            boolean wrap = config.wrapArrays(array);

            if (wrap) {
                indent++;
                addNewline();
            }

            int size = array.size();
            for (JsonNode node : array) {
                writeValue(node);
                if (size != 1 || config.json5() && config.addTrailingComma()) {
                    writeComma();
                }
                if (size != 1 && wrap) {
                    addNewline();
                }
                size--;
            }

            nextSpacing = Math.max(nextSpacing, config.spacesWithinArray());
            if (wrap) {
                indent--;
                addNewline();
            }
            addSpacing(0);
            output.append(']');
            nextSpacing = config.spacesAroundArray();
        }
    }

    private void writeObject(JsonNode object) throws IOException {
        if (object.size() == 0) {
            addSpacing(config.spacesAroundObject());
            output.append('{');
            addSpacing(config.spacesWithinEmptyObject());
            output.append('}');
            nextSpacing = config.spacesAroundObject();
        } else {
            addSpacing(config.spacesAroundObject());
            output.append('{');
            nextSpacing = config.spacesWithinObject();

            int alignmentLen = getObjectKeyAlignmentLength(object);
            boolean wrap = config.wrapArrays(object);

            if (wrap) {
                indent++;
                addNewline();
            }

            int size = object.size();
            for (String key : object.keys()) {
                String jsonKey = keyToJson(key);
                writeAlignedKey(jsonKey, alignmentLen);
                writeValue(object.get(key));
                if (size != 1 || config.json5() && config.addTrailingComma()) {
                    writeComma();
                }
                if (size != 1 && wrap) {
                    addNewline();
                }
                size--;
            }

            nextSpacing = Math.max(nextSpacing, config.spacesWithinObject());
            if (wrap) {
                indent--;
                addNewline();
            }
            addSpacing(0);
            output.append('}');
            nextSpacing = config.spacesAroundObject();
        }
    }

    private void writeAlignedKey(String key, int size) throws IOException {
        addSpacing(0);
        output.append(key);
        writeColon();

        if (!config.alignObjectValues())
            return;

        int len = key.length();
        int remainingSpace = Math.max(size - len, 0);
        addSpaces(remainingSpace); // Keep the spacing of the colon here, otherwise it might not align properly
    }

    private void writeValue(JsonNode value) throws IOException {
        if (value.isNull()) writeNull();
        else if (value.isBoolean()) writeBoolean(value.asBoolean());
        else if (value.isNumber()) writeNumber(value.asBigDecimal());
        else if (value.isString()) writeString(value.asString());
        else if (value.isArray()) writeArray(value);
        else if (value.isObject()) writeObject(value);
        else assert false; // Cannot happen if correctly implemented
    }

    private void writeJson(JsonNode node) throws IOException {
        if (config.makeNonExecutable()) {
            output.append(CharUtil.NOEXEC_LF);
        }

        node.require(JsonType.ARRAY, JsonType.OBJECT);
        writeValue(node);
        if (config.newlineAtEnd()) {
            addNewline();
        }

        validIds.clear();
        stringToJsonCache.clear();
    }

    static void serialize(JsonNode node, Appendable output, FormattingConfig config) {
        try {
            Serializer serializer = SERIALIZER_INSTANCE.get();
            serializer.reset(output, config);
            serializer.writeJson(node);
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }
}
