package dev.runefox.json.impl.parse.json;

import dev.runefox.json.impl.parse.TokenType;

public enum JsonTokenType implements TokenType {
    BOOLEAN("boolean", true, false),
    STRING("string", true, true),
    IDENTIFIER("identifier", false, true),
    NUMBER("number", true, false),
    NULL("null", true, false),
    OBJECT_START("'{'", true, false),
    OBJECT_END("'}'", false, false),
    ARRAY_START("'['", true, false),
    ARRAY_END("']'", false, false),
    COLON("':'", false, false),
    COMMA("','", false, false),
    EOF("EOF", false, false);

    private final String errorName;
    private final boolean isValue;
    private final boolean isKey;

    JsonTokenType(String name, boolean isValue, boolean isKey) {
        this.errorName = name;
        this.isValue = isValue;
        this.isKey = isKey;
    }

    public String getErrorName() {
        return errorName;
    }

    public boolean isValue() {
        return isValue;
    }

    public boolean isKey() {
        return isKey;
    }
}
