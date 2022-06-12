package net.shadew.json.template.parser;

enum TokenType {
    BOOLEAN("boolean"),
    IDENTIFIER("identifier"),
    NUMBER("number"),

    // Symbols
    PAREN_OPEN("'('"),
    PAREN_CLOSE("')'"),
    BRACKET_OPEN("'['"),
    BRACKET_CLOSE("']'"),
    BRACE_OPEN("'{'"),
    BRACE_CLOSE("'}'"),
    COMMA("','"),
    COLON("':'"),
    PLUS("'+'"),
    DASH("'-'"),
    STAR("'*'"),
    SLASH("'/'"),
    PERCENT("'%'"),
    PERIOD("'.'"),
    DOUBLE_PERIOD("'..'"),
    EXCL("'!'"),
    TILDE("'~'"),
    HASH("'#'"),
    LSH("'<<'"),
    RSH("'>>'"),
    RRSH("'>>>'"),
    LESS_THAN("'<'"),
    GREATER_THAN("'>'"),
    LESS_EQUAL("'<='"),
    GREATER_EQUAL("'>='"),
    EQUAL("'=='"),
    INEQUAL("'!='"),
    AND("'&'"),
    OR("'|'"),
    XOR("'^'"),
    AND2("'&&'"),
    OR2("'||'"),
    QUESTION("'?'"),
    AT("'@'"),
    PLUS2("'++'"),
    MINUS2("'--'"),
    ASSIGN("'='"),
    PLUS_IS("'+='"),
    MINUS_IS("'-='"),
    STAR_IS("'*='"),
    SLASH_IS("'/='"),
    PERCENT_IS("'%='"),
    LSH_IS("'<<='"),
    RSH_IS("'>>='"),
    RRSH_IS("'>>>='"),
    AND_IS("'&='"),
    OR_IS("'|='"),
    XOR_IS("'^='"),
    ARROW("'->'"),

    UNDERSCORE("'_'"),
    DOLLAR("'$'"),
    NULL("'null'"),
    COPY("'copy'"),
    IS("'is'"),
    ISNT("'isnt'"),
    HAS("'has'"),
    HASNT("'hasnt'"),
    IF("'if'"),
    ELSE("'else'"),
    FOR("'for'"),
    IN("'in'"),
    FROM("'from'"),
    TO("'to'"),
    SWITCH("'switch'"),
    MATCH("'match'"),
    CASE("'case'"),
    DO("'do'"),
    THEN("'then'"),
    DEF("'def'"),
    GEN("'gen'"),

    PURE_STRING("string"),
    DQ_DELIMITER("string delimiter"),
    ML_DELIMITER("string delimiter"),
    DQ_ML_DELIMITER("string delimiter"),
    STRING_CONTENT("string content"),
    INTERPOLATION("'#['"),
    ML_WHITESPACE("whitespace"),
    ML_LINE_BREAK("line break"),
    ML_BOUNDARY_INDICATOR("'\\'"),
    ML_NO_LINE_BREAK("'\\~'"),

    EOF("EOF");

    private final String errorName;

    TokenType(String name) {
        this.errorName = name;
    }

    String getErrorName() {
        return errorName;
    }
}
