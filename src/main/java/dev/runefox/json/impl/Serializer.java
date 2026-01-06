/*
 * Copyright 2022-2026 O. W. Nankman
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "
 * AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package dev.runefox.json.impl;

import dev.runefox.json.JsonNode;
import dev.runefox.json.JsonSerializingConfig;
import dev.runefox.json.NodeType;
import dev.runefox.json.SerializationException;
import dev.runefox.json.impl.node.NumberNode;
import dev.runefox.json.impl.node.StringNode;
import dev.runefox.json.impl.parse.CharUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Serializer {
    private static final ThreadLocal<Serializer> SERIALIZER_INSTANCE = ThreadLocal.withInitial(Serializer::new);
    private Appendable output;
    private JsonSerializingConfig config;
    private int nextSpacing;
    private final Set<String> validIds = new HashSet<>();
    private final Map<String, String> stringToJsonCache = new HashMap<>();
    private final StringBuilder builder = new StringBuilder();
    private char quote;

    private int indent = 0;

    public Serializer() {

    }

    public void reset(Appendable output, JsonSerializingConfig config) {
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
        for (String key : object.keySet()) {
            String jsonKey = keyToJson(key);
            len = Math.max(len, jsonKey.length());
        }
        return len;
    }

    private void writeString(String str) throws IOException {
        addSpacing(0);
        output.append(stringToJson(str));
    }

    private void writeNumber(NumberNode number) throws IOException {
        switch (number.finiteness()) {
            case FINITE -> writeFiniteNumber(number.asNumber(), number);
            case NAN -> writeNonFiniteNumber("NaN");
            case POSITIVE_INFINITE -> writeNonFiniteNumber("Infinity");
            case NEGATIVE_INFINITE -> writeNonFiniteNumber("-Infinity");
        }
    }

    private void writeNonFiniteNumber(String value) throws IOException {
        if (config.json5() && config.allowNonFiniteNumbers()) {
            output.append(value);
        } else {
            throw new IOException("Cannot serialize " + value + " as non-finite numbers are not supported");
        }
    }

    private static final Pattern TRAILING_ZERO_PATTERN = Pattern.compile("^(.*)\\.0+$");
    private static final Pattern TRAILING_ZERO_EXP_PATTERN = Pattern.compile("^(.*)\\.0+(e.*)$");

    private void writeFiniteNumber(Number number, NumberNode node) throws IOException {
        addSpacing(0);

        String str;
        if (number instanceof UnparsedNumber un) {
            str = un.toJsonValidString().toLowerCase();
        } else if (number instanceof UnparsedHexNumber uhn) {
            str = uhn.toJsonValidString().toLowerCase();
        } else if (number instanceof KotlinUnsignedIntWrapper kuiw) {
            str = kuiw.represent().toLowerCase();
        } else {
            str = node.asBigDecimal().toString().toLowerCase();
        }

        if (config.enforcePointInNumbers()) {
            if (!str.contains(".") && !str.contains("e"))
                output.append(str).append(".0");
            else if (!str.contains(".") && str.contains("e"))
                output.append(str.replace("e", ".0e"));
            else
                output.append(str);
        } else {
            // Try print without decimal point if string contains one
            Matcher m = TRAILING_ZERO_PATTERN.matcher(str);
            if (m.matches()) {
                output.append(m.group(1));
            } else {
                m = TRAILING_ZERO_EXP_PATTERN.matcher(str);
                if (m.matches()) {
                    output.append(m.group(1)).append(m.group(2));
                } else {
                    output.append(str);
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

            boolean wrap = config.shouldWrap(array);

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
            boolean wrap = config.shouldWrap(object);

            if (wrap) {
                indent++;
                addNewline();
            }

            int size = object.size();
            for (String key : object.keySet()) {
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
        switch (value.type()) {
            case NULL -> writeNull();
            case BOOLEAN -> writeBoolean(value.asBoolean());
            case NUMBER -> writeNumber((NumberNode) value);
            case STRING -> writeString(value.asString());
            case ARRAY -> writeArray(value);
            case OBJECT -> writeObject(value);
        }
    }

    public void writeJson(JsonNode node) throws IOException {
        if (config.makeNonExecutable()) {
            output.append(CharUtil.NOEXEC_LF);
        }

        if (!config.anyValue() && !node.is(NodeType.ARRAY, NodeType.OBJECT)) {
            throw new SerializationException("JSON document must be array or object to serialize");
        }

        writeValue(node);
        if (config.newlineAtEnd()) {
            addNewline();
        }

        validIds.clear();
        stringToJsonCache.clear();
    }

    public static void serialize(JsonNode node, Appendable output, JsonSerializingConfig config) throws IOException {
        Serializer serializer = SERIALIZER_INSTANCE.get();
        serializer.reset(output, config);
        serializer.writeJson(node);
    }
}
