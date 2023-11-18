package dev.runefox.json;

import java.util.function.Predicate;

/**
 * Configuration of JSON serialization.
 */
public class JsonSerializingConfig {
    private JsonSerializingConfig() {
    }

    private boolean json5 = false;

    public boolean json5() {
        return json5;
    }

    public JsonSerializingConfig json5(boolean json5) {
        this.json5 = json5;
        return this;
    }

    private boolean anyValue;

    public boolean anyValue() {
        return anyValue;
    }

    public JsonSerializingConfig anyValue(boolean anyValue) {
        this.anyValue = anyValue;
        return this;
    }

    private Predicate<JsonNode> wrapArrays = node -> true;
    private Predicate<JsonNode> wrapObjects = node -> true;

    public boolean wrapArrays(JsonNode node) {
        return wrapArrays.test(node);
    }

    public JsonSerializingConfig wrapArrays(boolean wrap) {
        this.wrapArrays = node -> wrap;
        return this;
    }

    public JsonSerializingConfig wrapArrays(Predicate<JsonNode> wrap) {
        this.wrapArrays = wrap;
        return this;
    }

    public boolean wrapObjects(JsonNode node) {
        return wrapObjects.test(node);
    }

    public JsonSerializingConfig wrapObjects(boolean wrap) {
        this.wrapObjects = node -> wrap;
        return this;
    }

    public JsonSerializingConfig wrapObjects(Predicate<JsonNode> wrap) {
        this.wrapObjects = wrap;
        return this;
    }

    private boolean ensurePointInNumbers = false;

    public boolean ensurePointInNumbers() {
        return ensurePointInNumbers;
    }

    public JsonSerializingConfig ensurePointInNumbers(boolean enabled) {
        this.ensurePointInNumbers = enabled;
        return this;
    }

    private boolean useSingleQuoteStrings = false;
    private boolean useIdentifierKeys = true;

    public boolean useSingleQuoteStrings() {
        return useSingleQuoteStrings;
    }

    public JsonSerializingConfig useSingleQuoteStrings(boolean enable) {
        this.useSingleQuoteStrings = enable;
        return this;
    }

    public boolean useIdentifierKeys() {
        return useIdentifierKeys;
    }

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

    public int spacesAroundArray() {
        return spacesAroundArray;
    }

    public JsonSerializingConfig spacesAroundArray(int spacing) {
        if (spacing < 0) throw new IllegalArgumentException("Negative spacing");
        this.spacesAroundArray = spacing;
        return this;
    }

    public int spacesWithinArray() {
        return spacesWithinArray;
    }

    public JsonSerializingConfig spacesWithinArray(int spacing) {
        if (spacing < 0) throw new IllegalArgumentException("Negative spacing");
        this.spacesWithinArray = spacing;
        return this;
    }

    public int spacesWithinEmptyArray() {
        return spacesWithinEmptyArray;
    }

    public JsonSerializingConfig spacesWithinEmptyArray(int spacing) {
        if (spacing < 0) throw new IllegalArgumentException("Negative spacing");
        this.spacesWithinEmptyArray = spacing;
        return this;
    }

    public JsonSerializingConfig arraySpacing(int around, int within, int inEmpty) {
        if (around < 0) throw new IllegalArgumentException("Negative around array spacing");
        if (within < 0) throw new IllegalArgumentException("Negative within array spacing");
        if (inEmpty < 0) throw new IllegalArgumentException("Negative empty array spacing");

        this.spacesAroundArray = around;
        this.spacesWithinArray = within;
        this.spacesWithinEmptyArray = inEmpty;
        return this;
    }

    public int spacesAroundObject() {
        return spacesAroundObject;
    }

    public JsonSerializingConfig spacesAroundObject(int spacing) {
        if (spacing < 0) throw new IllegalArgumentException("Negative spacing");
        this.spacesAroundObject = spacing;
        return this;
    }

    public int spacesWithinObject() {
        return spacesWithinObject;
    }

    public JsonSerializingConfig spacesWithinObject(int spacing) {
        if (spacing < 0) throw new IllegalArgumentException("Negative spacing");
        this.spacesWithinObject = spacing;
        return this;
    }

    public int spacesWithinEmptyObject() {
        return spacesWithinEmptyObject;
    }

    public JsonSerializingConfig spacesWithinEmptyObject(int spacing) {
        if (spacing < 0) throw new IllegalArgumentException("Negative spacing");
        this.spacesWithinEmptyObject = spacing;
        return this;
    }

    public JsonSerializingConfig objectSpacing(int around, int within, int inEmpty) {
        if (around < 0) throw new IllegalArgumentException("Negative around object spacing");
        if (within < 0) throw new IllegalArgumentException("Negative within object spacing");
        if (inEmpty < 0) throw new IllegalArgumentException("Negative empty object spacing");

        this.spacesAroundObject = around;
        this.spacesWithinObject = within;
        this.spacesWithinEmptyObject = inEmpty;
        return this;
    }

    public int spacesBeforeComma() {
        return spacesBeforeComma;
    }

    public JsonSerializingConfig spacesBeforeComma(int spacing) {
        if (spacing < 0) throw new IllegalArgumentException("Negative spacing");
        this.spacesBeforeComma = spacing;
        return this;
    }

    public int spacesAfterComma() {
        return spacesAfterComma;
    }

    public JsonSerializingConfig spacesAfterComma(int spacing) {
        if (spacing < 0) throw new IllegalArgumentException("Negative spacing");
        this.spacesAfterComma = spacing;
        return this;
    }

    public JsonSerializingConfig commaSpacing(int before, int after) {
        if (before < 0) throw new IllegalArgumentException("Negative before comma spacing");
        if (after < 0) throw new IllegalArgumentException("Negative after comma spacing");

        this.spacesBeforeComma = before;
        this.spacesAfterComma = after;
        return this;
    }

    public int spacesBeforeColon() {
        return spacesBeforeColon;
    }

    public JsonSerializingConfig spacesBeforeColon(int spacing) {
        if (spacing < 0) throw new IllegalArgumentException("Negative spacing");
        this.spacesBeforeColon = spacing;
        return this;
    }

    public int spacesAfterColon() {
        return spacesAfterColon;
    }

    public JsonSerializingConfig spacesAfterColon(int spacing) {
        if (spacing < 0) throw new IllegalArgumentException("Negative spacing");
        this.spacesAfterColon = spacing;
        return this;
    }

    public JsonSerializingConfig colonSpacing(int before, int after) {
        if (before < 0) throw new IllegalArgumentException("Negative before colon spacing");
        if (after < 0) throw new IllegalArgumentException("Negative after colon spacing");

        this.spacesBeforeColon = before;
        this.spacesAfterColon = after;
        return this;
    }

    private boolean alignObjectValues = false;

    public boolean alignObjectValues() {
        return alignObjectValues;
    }

    public JsonSerializingConfig alignObjectValues(boolean enabled) {
        this.alignObjectValues = enabled;
        return this;
    }

    private boolean addTrailingComma = false;

    public boolean addTrailingComma() {
        return addTrailingComma;
    }

    public JsonSerializingConfig addTrailingComma(boolean enabled) {
        this.addTrailingComma = enabled;
        return this;
    }

    private int indent = 4;
    private boolean tabIndent = false;

    public int indent() {
        return indent;
    }

    public JsonSerializingConfig indent(int indent) {
        if (indent < 0) throw new IllegalArgumentException("Negative indent");
        this.indent = indent;
        return this;
    }

    public boolean tabIndent() {
        return tabIndent;
    }

    public JsonSerializingConfig tabIndent(boolean tab) {
        this.tabIndent = tab;
        return this;
    }

    private LineSeparator lineSeparator = LineSeparator.SYSTEM;

    public LineSeparator lineSeparator() {
        return lineSeparator;
    }

    public JsonSerializingConfig lineSeparator(LineSeparator lineSeparator) {
        if (lineSeparator == null) throw new NullPointerException();
        this.lineSeparator = lineSeparator;
        return this;
    }

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

    public boolean newlineAtEnd() {
        return newlineAtEnd;
    }

    public JsonSerializingConfig newlineAtEnd(boolean enabled) {
        this.newlineAtEnd = enabled;
        return this;
    }

    private boolean makeNonExecutable = false;

    public boolean makeNonExecutable() {
        return makeNonExecutable;
    }

    public JsonSerializingConfig makeNonExecutable(boolean enabled) {
        this.makeNonExecutable = enabled;
        return this;
    }

    public JsonSerializingConfig copy() {
        return new JsonSerializingConfig().copyFrom(this);
    }

    public JsonSerializingConfig copyFrom(JsonSerializingConfig copy) {
        if (copy == null)
            throw new NullPointerException();

        this.json5 = copy.json5;
        this.anyValue = copy.anyValue;
        this.wrapArrays = copy.wrapArrays;
        this.wrapObjects = copy.wrapObjects;
        this.ensurePointInNumbers = copy.ensurePointInNumbers;
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

    public static JsonSerializingConfig pretty() {
        return new JsonSerializingConfig();
    }

    public static JsonSerializingConfig prettyCompact() {
        return pretty().wrapArrays(false)
                       .wrapObjects(false)
                       .newlineAtEnd(false);
    }

    public static JsonSerializingConfig compact() {
        return prettyCompact().commaSpacing(0, 0)
                              .colonSpacing(0, 0)
                              .arraySpacing(0, 0, 0)
                              .objectSpacing(0, 0, 0)
                              .useIdentifierKeys(true);
    }

    public enum LineSeparator {
        CR("\r"),
        LF("\n"),
        CRLF("\r\n"),
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
