package net.shadew.json.template.parser;

enum TokenType implements ParserNodeType {
    BOOLEAN("[bool]", "boolean"),
    IDENTIFIER("[id]", "identifier"),
    NUMBER("[num]", "number"),

    // Symbols
    PAREN_OPEN("'('", "'('"),
    PAREN_CLOSE("')'", "')'"),
    BRACKET_OPEN("'['", "'['"),
    BRACKET_CLOSE("']'", "']'"),
    BRACE_OPEN("'{'", "'{'"),
    BRACE_CLOSE("'}'", "'}'"),
    COMMA("','", "','"),
    COLON("':'", "':'"),
    PLUS("'+'", "'+'"),
    DASH("'-'", "'-'"),
    STAR("'*'", "'*'"),
    SLASH("'/'", "'/'"),
    PERCENT("'%'", "'%'"),
    PERIOD("'.'", "'.'"),
    DOUBLE_PERIOD("'..'", "'..'"),
    TRIPLE_PERIOD("'...'", "'...'"),
    EXCL("'!'", "'!'"),
    TILDE("'~'", "'~'"),
    HASH("'#'", "'#'"),
    LSH("'<<'", "'<<'"),
    RSH("'>>'", "'>>'"),
    RRSH("'>>>'", "'>>>'"),
    LESS_THAN("'<'", "'<'"),
    GREATER_THAN("'>'", "'>'"),
    LESS_EQUAL("'<='", "'<='"),
    GREATER_EQUAL("'>='", "'>='"),
    EQUAL("'=='", "'=='"),
    INEQUAL("'!='", "'!='"),
    AND("'&'", "'&'"),
    OR("'|'", "'|'"),
    XOR("'^'", "'^'"),
    AND2("'&&'", "'&&'"),
    OR2("'||'", "'||'"),
    QUESTION("'?'", "'?'"),
    AT("'@'", "'@'"),
    PLUS2("'++'", "'++'"),
    MINUS2("'--'", "'--'"),
    ASSIGN("'='", "'='"),
    PLUS_IS("'+='", "'+='"),
    MINUS_IS("'-='", "'-='"),
    STAR_IS("'*='", "'*='"),
    SLASH_IS("'/='", "'/='"),
    PERCENT_IS("'%='", "'%='"),
    LSH_IS("'<<='", "'<<='"),
    RSH_IS("'>>='", "'>>='"),
    RRSH_IS("'>>>='", "'>>>='"),
    AND_IS("'&='", "'&='"),
    OR_IS("'|='", "'|='"),
    XOR_IS("'^='", "'^='"),
    ARROW("'->'", "'->'"),

    UNDERSCORE("_", "'_'"),
    DOLLAR("$", "'$'"),
    NULL("null", "'null'"),
    COPY("copy", "'copy'"),
    IS("is", "'is'"),
    ISNT("isnt", "'isnt'"),
    HAS("has", "'has'"),
    HASNT("hasnt", "'hasnt'"),
    IF("if", "'if'"),
    ELSE("else", "'else'"),
    FOR("for", "'for'"),
    IN("in", "'in'"),
    FROM("from", "'from'"),
    TO("to", "'to'"),
    SWITCH("switch", "'switch'"),
    MATCH("match", "'match'"),
    CASE("case", "'case'"),
    DO("do", "'do'"),
    THEN("then", "'then'"),
    DEF("def", "'def'"),
    GEN("gen", "'gen'"),

    PURE_STRING("[str]", "string"),
    DQ_DELIMITER("[str:dq]", "string delimiter"),
    ML_DELIMITER("[str:sqml]", "string delimiter"),
    DQ_ML_DELIMITER("[str:dqml]", "string delimiter"),
    STRING_CONTENT("[str:content]", "string content"),
    INTERPOLATION("'#['", "'#['"),
    ML_WHITESPACE("[ws]", "whitespace"),
    ML_LINE_BREAK("[nl]", "line break"),
    ML_BOUNDARY_INDICATOR("'\\'", "'\\'"),
    ML_NO_LINE_BREAK("'\\~'", "'\\~'"),

    EOF("[eof]", "EOF");

    public static final int AMOUNT = values().length;

    private final String ruleDefinitionName;
    private final String errorName;

    TokenType(String ruleDefinitionName, String name) {
        this.ruleDefinitionName = ruleDefinitionName;
        this.errorName = name;
    }

    @Override
    public String ruleDefinitionName() {
        return ruleDefinitionName;
    }

    @Override
    public String errorName() {
        return errorName;
    }

    @Override
    public boolean isTerminal() {
        return true;
    }

    @Override
    public TokenType terminal() {
        return this;
    }

    @Override
    public NodeType nonterminal() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return ruleDefinitionName();
    }
}
