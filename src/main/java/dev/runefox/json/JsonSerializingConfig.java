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

package dev.runefox.json;

import java.util.function.Predicate;

/**
 * Configuration of JSON serialization.
 */
public class JsonSerializingConfig {
    private JsonSerializingConfig() {
    }

    private boolean json5 = false;

    /**
     * Whether the document may be encoded using JSON5 syntax.
     */
    public boolean json5() {
        return json5;
    }

    /**
     * Set whether the document may be encoded using JSON5 syntax.
     */
    public JsonSerializingConfig json5(boolean json5) {
        this.json5 = json5;
        return this;
    }

    private boolean anyValue;

    /**
     * Whether the document may be any JSON value, not just objects or arrays.
     */
    public boolean anyValue() {
        return anyValue;
    }

    /**
     * Set whether the document may be any JSON value, not just objects or arrays.
     */
    public JsonSerializingConfig anyValue(boolean anyValue) {
        this.anyValue = anyValue;
        return this;
    }

    private boolean allowNonFiniteNumbers = true;

    /**
     * Whether {@code NaN} and {@code Infinity} are accepted as valid number values. Only effective if {@link #json5() JSON5} is enabled.
     * If JSON5 is disabled or this is false, a {@link SerializationException} is thrown for any {@code NaN} or {@code Infinity} value.
     */
    public boolean allowNonFiniteNumbers() {
        return allowNonFiniteNumbers;
    }

    /**
     * Set whether {@code NaN} and {@code Infinity} are accepted as valid number values. Only effective if {@link #json5() json5} is enabled.
     * If JSON5 is disabled or this is false, a {@link SerializationException} is thrown for any {@code NaN} or {@code Infinity} value.
     */
    public JsonSerializingConfig allowNonFiniteNumbers(boolean allowNonFiniteNumbers) {
        this.allowNonFiniteNumbers = allowNonFiniteNumbers;
        return this;
    }

    private Predicate<JsonNode> wrapArrays = node -> true;
    private Predicate<JsonNode> wrapObjects = node -> true;


    /**
     * Check whether the given JSON node should be wrapped, that is, its entries are printed on separate lines.
     */
    public boolean shouldWrap(JsonNode node) {
        if (node.isObject()) {
            return wrapObjects.test(node);
        } else if (node.isArray()) {
            return wrapArrays.test(node);
        } else {
            return false;
        }
    }

    /**
     * Set whether arrays should be wrapped, that is, whether its entries should be printed on separate lines.
     */
    public JsonSerializingConfig wrapArrays(boolean wrap) {
        this.wrapArrays = node -> wrap;
        return this;
    }

    /**
     * Set a predicate that determines if an array should be wrapped, that is, whether its entries should be printed on
     * separate lines.
     */
    public JsonSerializingConfig wrapArrays(Predicate<JsonNode> wrap) {
        this.wrapArrays = wrap;
        return this;
    }


    /**
     * Set whether objects should be wrapped, that is, whether its entries should be printed on separate lines.
     */
    public JsonSerializingConfig wrapObjects(boolean wrap) {
        this.wrapObjects = node -> wrap;
        return this;
    }

    /**
     * Set a predicate that determines if an object should be wrapped, that is, whether its entries should be printed on
     * separate lines.
     */
    public JsonSerializingConfig wrapObjects(Predicate<JsonNode> wrap) {
        this.wrapObjects = wrap;
        return this;
    }

    private boolean enforcePointInNumbers = false;

    /**
     * Whether to enforce a decimal point in serialized number literals. When true, the value 1 will be printed as 1.0.
     * When false, no unnecessary decimal point will be printed.
     */
    public boolean enforcePointInNumbers() {
        return enforcePointInNumbers;
    }

    /**
     * Set whether to enforce a decimal point in serialized number literals. When true, the value 1 will be printed as
     * 1.0. When false, no unnecessary decimal point will be printed.
     */
    public JsonSerializingConfig enforcePointInNumbers(boolean enabled) {
        this.enforcePointInNumbers = enabled;
        return this;
    }

    private boolean useSingleQuoteStrings = false;
    private boolean useIdentifierKeys = true;

    /**
     * Whether to print strings with single quotes ({@code '}). Only works with {@link #json5() json5}.
     */
    public boolean useSingleQuoteStrings() {
        return useSingleQuoteStrings;
    }

    /**
     * Set whether to print strings with single quotes ({@code '}). Only works with {@link #json5() json5}.
     */
    public JsonSerializingConfig useSingleQuoteStrings(boolean enable) {
        this.useSingleQuoteStrings = enable;
        return this;
    }

    /**
     * Whether to print object keys as identifiers where possible (e.g. {@code foo} instead of {@code "foo"}). Only
     * works with {@link #json5() json5}.
     */
    public boolean useIdentifierKeys() {
        return useIdentifierKeys;
    }

    /**
     * Set whether to print object keys as identifiers where possible (e.g. {@code foo} instead of {@code "foo"}). Only
     * works with {@link #json5() json5}.
     */
    public JsonSerializingConfig useIdentifierKeys(boolean enable) {
        this.useIdentifierKeys = enable;
        return this;
    }

    private int spacesAroundArray = 0;
    private int spacesWithinArray = 1;
    private int spacesWithinEmptyArray = 0;
    private int spacesAroundObject = 0;
    private int spacesWithinObject = 1;
    private int spacesWithinEmptyObject = 0;
    private int spacesBeforeComma = 0;
    private int spacesAfterComma = 1;
    private int spacesBeforeColon = 0;
    private int spacesAfterColon = 1;

    /**
     * Amount of spaces to add before the <code>[</code> and after the <code>]</code> of an array.
     */
    public int spacesAroundArray() {
        return spacesAroundArray;
    }

    /**
     * Set the amount of spaces to add before the <code>[</code> and after the <code>]</code> of an array.
     */
    public JsonSerializingConfig spacesAroundArray(int spacing) {
        if (spacing < 0) throw new IllegalArgumentException("Negative spacing");
        this.spacesAroundArray = spacing;
        return this;
    }

    /**
     * Amount of spaces to add after the <code>[</code> and before the <code>]</code> of an array with elements.
     */
    public int spacesWithinArray() {
        return spacesWithinArray;
    }

    /**
     * Set the amount of spaces to add after the <code>[</code> and before the <code>]</code> of an array with elements.
     */
    public JsonSerializingConfig spacesWithinArray(int spacing) {
        if (spacing < 0) throw new IllegalArgumentException("Negative spacing");
        this.spacesWithinArray = spacing;
        return this;
    }

    /**
     * Amount of spaces to add between the <code>[</code> and the <code>]</code> of an array with no elements.
     */
    public int spacesWithinEmptyArray() {
        return spacesWithinEmptyArray;
    }

    /**
     * Set the amount of spaces to add between the <code>[</code> and the <code>]</code> of an array with no elements.
     */
    public JsonSerializingConfig spacesWithinEmptyArray(int spacing) {
        if (spacing < 0) throw new IllegalArgumentException("Negative spacing");
        this.spacesWithinEmptyArray = spacing;
        return this;
    }

    /**
     * Sets {@link #spacesAroundArray()}, {@link #spacesWithinArray()} and {@link #spacesWithinEmptyArray()}
     * simultaneously.
     */
    public JsonSerializingConfig arraySpacing(int around, int within, int inEmpty) {
        if (around < 0) throw new IllegalArgumentException("Negative around array spacing");
        if (within < 0) throw new IllegalArgumentException("Negative within array spacing");
        if (inEmpty < 0) throw new IllegalArgumentException("Negative empty array spacing");

        this.spacesAroundArray = around;
        this.spacesWithinArray = within;
        this.spacesWithinEmptyArray = inEmpty;
        return this;
    }

    /**
     * Amount of spaces to add before the <code>{</code> and after the <code>}</code> of an object.
     */
    public int spacesAroundObject() {
        return spacesAroundObject;
    }

    /**
     * Set the amount of spaces to add before the <code>{</code> and after the <code>}</code> of an object.
     */
    public JsonSerializingConfig spacesAroundObject(int spacing) {
        if (spacing < 0) throw new IllegalArgumentException("Negative spacing");
        this.spacesAroundObject = spacing;
        return this;
    }

    /**
     * Amount of spaces to add after the <code>{</code> and before the <code>}</code> of an object with elements.
     */
    public int spacesWithinObject() {
        return spacesWithinObject;
    }

    /**
     * Set the amount of spaces to add after the <code>{</code> and before the <code>}</code> of an object with elements.
     */
    public JsonSerializingConfig spacesWithinObject(int spacing) {
        if (spacing < 0) throw new IllegalArgumentException("Negative spacing");
        this.spacesWithinObject = spacing;
        return this;
    }

    /**
     * Amount of spaces to add between the <code>{</code> and the <code>}</code> of an object with no elements.
     */
    public int spacesWithinEmptyObject() {
        return spacesWithinEmptyObject;
    }

    /**
     * Set the amount of spaces to add between the <code>{</code> and the <code>}</code> of an object with no elements.
     */
    public JsonSerializingConfig spacesWithinEmptyObject(int spacing) {
        if (spacing < 0) throw new IllegalArgumentException("Negative spacing");
        this.spacesWithinEmptyObject = spacing;
        return this;
    }

    /**
     * Sets {@link #spacesAroundObject()}, {@link #spacesWithinObject()} and {@link #spacesWithinEmptyObject()}
     * simultaneously.
     */
    public JsonSerializingConfig objectSpacing(int around, int within, int inEmpty) {
        if (around < 0) throw new IllegalArgumentException("Negative around object spacing");
        if (within < 0) throw new IllegalArgumentException("Negative within object spacing");
        if (inEmpty < 0) throw new IllegalArgumentException("Negative empty object spacing");

        this.spacesAroundObject = around;
        this.spacesWithinObject = within;
        this.spacesWithinEmptyObject = inEmpty;
        return this;
    }

    /**
     * Amount of spaces to add before any <code>,</code>.
     */
    public int spacesBeforeComma() {
        return spacesBeforeComma;
    }

    /**
     * Set the amount of spaces to add before any <code>,</code>.
     */
    public JsonSerializingConfig spacesBeforeComma(int spacing) {
        if (spacing < 0) throw new IllegalArgumentException("Negative spacing");
        this.spacesBeforeComma = spacing;
        return this;
    }

    /**
     * Amount of spaces to add after any <code>,</code>.
     */
    public int spacesAfterComma() {
        return spacesAfterComma;
    }

    /**
     * Set the amount of spaces to add after any <code>,</code>.
     */
    public JsonSerializingConfig spacesAfterComma(int spacing) {
        if (spacing < 0) throw new IllegalArgumentException("Negative spacing");
        this.spacesAfterComma = spacing;
        return this;
    }

    /**
     * Set the amount of spaces to add before and after any <code>,</code>.
     */
    public JsonSerializingConfig commaSpacing(int before, int after) {
        if (before < 0) throw new IllegalArgumentException("Negative before comma spacing");
        if (after < 0) throw new IllegalArgumentException("Negative after comma spacing");

        this.spacesBeforeComma = before;
        this.spacesAfterComma = after;
        return this;
    }

    /**
     * Amount of spaces to add before any <code>:</code>.
     */
    public int spacesBeforeColon() {
        return spacesBeforeColon;
    }

    /**
     * Set the amount of spaces to add before any <code>:</code>.
     */
    public JsonSerializingConfig spacesBeforeColon(int spacing) {
        if (spacing < 0) throw new IllegalArgumentException("Negative spacing");
        this.spacesBeforeColon = spacing;
        return this;
    }

    /**
     * Amount of spaces to add after any <code>:</code>.
     */
    public int spacesAfterColon() {
        return spacesAfterColon;
    }

    /**
     * Set the amount of spaces to add after any <code>:</code>.
     */
    public JsonSerializingConfig spacesAfterColon(int spacing) {
        if (spacing < 0) throw new IllegalArgumentException("Negative spacing");
        this.spacesAfterColon = spacing;
        return this;
    }

    /**
     * Set the amount of spaces to add before and after any <code>:</code>.
     */
    public JsonSerializingConfig colonSpacing(int before, int after) {
        if (before < 0) throw new IllegalArgumentException("Negative before colon spacing");
        if (after < 0) throw new IllegalArgumentException("Negative after colon spacing");

        this.spacesBeforeColon = before;
        this.spacesAfterColon = after;
        return this;
    }

    private boolean alignObjectValues = false;

    /**
     * Whether to add extra spacing so that object values neatly align when displayed in a monospace font, like so:
     * <pre>{@code
     * {
     *     "foo-bar": 2,
     *     "baz":     "gus"
     * }}</pre>
     */
    public boolean alignObjectValues() {
        return alignObjectValues;
    }

    /**
     * Set whether to add extra spacing so that object values neatly align when displayed in a monospace font, like so:
     * <pre>{@code
     * {
     *     "foo-bar": 2,
     *     "baz":     "gus"
     * }}</pre>
     */
    public JsonSerializingConfig alignObjectValues(boolean enabled) {
        this.alignObjectValues = enabled;
        return this;
    }

    private boolean addTrailingComma = false;

    /**
     * Whether to add a trailing comma in arrays and objects which are not empty. Works only in {@link #json5() json5}.
     */
    public boolean addTrailingComma() {
        return addTrailingComma;
    }

    /**
     * Set whether to add a trailing comma in arrays and objects which are not empty. Works only in
     * {@link #json5() json5}.
     */
    public JsonSerializingConfig addTrailingComma(boolean enabled) {
        this.addTrailingComma = enabled;
        return this;
    }

    private int indent = 4;
    private boolean tabIndent = false;

    /**
     * Amount of indentation characters to insert. Indentation characters are spaces unless
     * {@link #tabIndent() tabIndent} is true.
     */
    public int indent() {
        return indent;
    }

    /**
     * Set the amount of indentation characters to insert. Indentation characters are spaces unless
     * {@link #tabIndent() tabIndent} is true.
     */
    public JsonSerializingConfig indent(int indent) {
        if (indent < 0) throw new IllegalArgumentException("Negative indent");
        this.indent = indent;
        return this;
    }

    /**
     * Whether to indent using tabs instead of spaces. Note that if {@link #indent() indent} is 4, then 4 tab characters
     * will be inserted.
     */
    public boolean tabIndent() {
        return tabIndent;
    }

    /**
     * Set whether to indent using tabs instead of spaces. Note that if {@link #indent() indent} is 4, then 4 tab
     * characters will be inserted.
     */
    public JsonSerializingConfig tabIndent(boolean tab) {
        this.tabIndent = tab;
        return this;
    }

    private LineSeparator lineSeparator = LineSeparator.SYSTEM;

    /**
     * Which line separator to use. See {@link LineSeparator}.
     */
    public LineSeparator lineSeparator() {
        return lineSeparator;
    }

    /**
     * Set which line separator to use. See {@link LineSeparator}.
     */
    public JsonSerializingConfig lineSeparator(LineSeparator lineSeparator) {
        if (lineSeparator == null) throw new NullPointerException();
        this.lineSeparator = lineSeparator;
        return this;
    }

    /**
     * Set which line separator to use. The following arguments are valid (case insensitive):
     * <ul>
     * <li>{@code "CRLF"} or {@code "\r\n"} for CRLF.</li>
     * <li>{@code "LF"} or {@code "\n"} for LF.</li>
     * <li>{@code "CR"} or {@code "\r"} for CR.</li>
     * <li>{@code "SYSTEM"} for {@link System#lineSeparator()}.</li>
     * </ul>
     * See {@link LineSeparator}.
     */
    public JsonSerializingConfig lineSeparator(String separator) {
        if (separator == null) throw new NullPointerException();
        switch (separator.toUpperCase()) {
            case "CRLF":
            case "\r\n":
                this.lineSeparator = LineSeparator.CRLF;
                break;
            case "LF":
            case "\n":
                this.lineSeparator = LineSeparator.LF;
                break;
            case "CR":
            case "\r":
                this.lineSeparator = LineSeparator.CR;
                break;
            case "SYSTEM":
                this.lineSeparator = LineSeparator.SYSTEM;
                break;
            default:
                throw new IllegalArgumentException("Line separaotr must be \\r\\n, \\n, \\r, CRLF, LF, CR or SYSTEM.");
        }
        return this;
    }

    private boolean newlineAtEnd = true;

    /**
     * Whether to add a new line after the document.
     */
    public boolean newlineAtEnd() {
        return newlineAtEnd;
    }

    /**
     * Set whether to add a new line after the document.
     */
    public JsonSerializingConfig newlineAtEnd(boolean enabled) {
        this.newlineAtEnd = enabled;
        return this;
    }

    private boolean makeNonExecutable = false;

    /**
     * Whether to add a non-execution prefix at the beginning of the document. This prevents the data to be interpreted
     * as a JavaScript script by browsers.
     */
    public boolean makeNonExecutable() {
        return makeNonExecutable;
    }

    /**
     * Set whether to add a non-execution prefix at the beginning of the document. This prevents the data to be
     * interpreted as a JavaScript script by browsers.
     */
    public JsonSerializingConfig makeNonExecutable(boolean enabled) {
        this.makeNonExecutable = enabled;
        return this;
    }

    /**
     * Create a copy of this configuration instance.
     */
    public JsonSerializingConfig copy() {
        return new JsonSerializingConfig().copyFrom(this);
    }

    /**
     * Copy the given configuration to this configuration instance.
     */
    public JsonSerializingConfig copyFrom(JsonSerializingConfig copy) {
        if (copy == null)
            throw new NullPointerException();

        this.json5 = copy.json5;
        this.anyValue = copy.anyValue;
        this.allowNonFiniteNumbers = copy.allowNonFiniteNumbers;
        this.wrapArrays = copy.wrapArrays;
        this.wrapObjects = copy.wrapObjects;
        this.enforcePointInNumbers = copy.enforcePointInNumbers;
        this.useSingleQuoteStrings = copy.useSingleQuoteStrings;
        this.useIdentifierKeys = copy.useIdentifierKeys;
        this.spacesAroundArray = copy.spacesAroundArray;
        this.spacesWithinArray = copy.spacesWithinArray;
        this.spacesWithinEmptyArray = copy.spacesWithinEmptyArray;
        this.spacesAroundObject = copy.spacesAroundObject;
        this.spacesWithinObject = copy.spacesWithinObject;
        this.spacesWithinEmptyObject = copy.spacesWithinEmptyObject;
        this.spacesBeforeComma = copy.spacesBeforeComma;
        this.spacesAfterComma = copy.spacesAfterComma;
        this.spacesBeforeColon = copy.spacesBeforeColon;
        this.spacesAfterColon = copy.spacesAfterColon;
        this.alignObjectValues = copy.alignObjectValues;
        this.addTrailingComma = copy.addTrailingComma;
        this.indent = copy.indent;
        this.tabIndent = copy.tabIndent;
        this.lineSeparator = copy.lineSeparator;
        this.newlineAtEnd = copy.newlineAtEnd;
        this.makeNonExecutable = copy.makeNonExecutable;
        return this;
    }

    /**
     * Pretty-printed serialization config. Arrays and objects wrap and indent, and spacing is added as is conventional.
     */
    public static JsonSerializingConfig pretty() {
        return new JsonSerializingConfig();
    }

    /**
     * Pretty-printed serialization config, but on a single line. No indentation or wrapping occurs, but spaces are
     * still inserted as conventional.
     */
    public static JsonSerializingConfig prettyCompact() {
        return pretty().wrapArrays(false)
                       .wrapObjects(false)
                       .newlineAtEnd(false);
    }

    /**
     * Compact serialization config. Serializer tries to write an as short as possible JSON string. No indentation or
     * wrapping occurs, spacing is minimal, and in JSON5 any valid identifier keys are printed without quotes.
     */
    public static JsonSerializingConfig compact() {
        return prettyCompact().commaSpacing(0, 0)
                              .colonSpacing(0, 0)
                              .arraySpacing(0, 0, 0)
                              .objectSpacing(0, 0, 0)
                              .useIdentifierKeys(true);
    }

    public enum LineSeparator {
        /** Carriage return. */
        CR("\r"),

        /** Line feed. Most commonly used. */
        LF("\n"),

        /** Carriage return, then line feed. Common on Windows. */
        CRLF("\r\n"),

        /** Use the system line separator, as per {@link System#lineSeparator()}. */
        SYSTEM(System.lineSeparator());

        private final String ls;

        LineSeparator(String ls) {
            this.ls = ls;
        }

        @Override
        public String toString() {
            return ls;
        }
    }
}
