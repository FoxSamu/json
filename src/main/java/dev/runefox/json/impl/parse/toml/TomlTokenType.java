package dev.runefox.json.impl.parse.toml;

import dev.runefox.json.impl.parse.TokenType;

public enum TomlTokenType implements TokenType {
    BOOLEAN("boolean"),
    STRING("string"),
    IDENTIFIER("identifier"),
    INTEGER("integer"),
    FLOAT("float"),
    OFFSET_DATE_TIME("offset date+time"),
    LOCAL_DATE_TIME("local date+time"),
    LOCAL_DATE("local date"),
    LOCAL_TIME("local time"),
    INLINE_TABLE_L("'{'"),
    INLINE_TABLE_R("'}'"),
    ARRAY_L("'['"),
    ARRAY_R("']'"),
    TABLE_HEADER_L("'['"),
    TABLE_HEADER_R("']'"),
    ARR_TABLE_HEADER_L("'[['"),
    ARR_TABLE_HEADER_R("']]'"),
    EQUALS("'='"),
    COMMA("','"),
    DOT("'.'"),
    EOL("EOL"),
    EOF("EOF");

    private final String errorName;

    TomlTokenType(String errorName) {
        this.errorName = errorName;
    }

    @Override
    public String errorName() {
        return errorName;
    }


}
