package net.shadew.json.template.parser;

public enum NodeType implements NonterminalType {
    DOCUMENT("document"),

    EXPR_LITERAL("literal"),
    EXPR_VARIABLE("variable"),
    EXPR_UNARY_OP("unary operation"),
    EXPR_BINARY_OP("binary operation"),
    EXPR_TERNARY_OP("ternary operation"),
    EXPR_HAS_KEY("'has' operation"),
    EXPR_IS_TYPE("'is' operation"),
    EXPR_LOGIC_OP("logic operation"),
    EXPR_INDEX("index reference"),
    EXPR_SLICE("slice operation"),
    EXPR_MEMBER("member reference"),
    EXPR_ASSIGN("assignment"),
    EXPR_INCR("increment"),
    EXPR_INTERPOLATE_STRING("interpolated string"),
    EXPR_DO_THEN("do-then"),
    EXPR_MATCH("match"),
    EXPR_OBJECT("object"),
    EXPR_ARRAY("array"),
    EXPR_SUBTEMPLATE("gen"),
    EXPR_CALL_FN("function call"),
    EXPR_UNDERSCORE("'_'"),
    EXPR_DOLLAR("'$'"),

    ENT_VALUE("value"),
    ENT_KEY_VALUE("key-value"),
    ENT_VOID_LINE("void line"),
    ENT_IF("if"),
    ENT_FOR_IN("for-in"),
    ENT_FOR_IN_OBJ("object for-in"),
    ENT_FOR_FROM_TO("for-from-to"),
    ENT_BREAK("break"),
    ENT_CONTINUE("continue"),
    ENT_RETURN("return"),
    ENT_SWITCH("switch"),
    ENT_DEF_EXPRESSION_FN("expression function"),
    ENT_DEF_SUBTEMPLATE_FN("subtemplate function"),

    BLOCK_ELSE_IF("else if"),
    BLOCK_ELSE("else"),
    BLOCK_CASE("case"),

    STRING_INTERPOLATION("interpolatin"),
    MATCH_CASE("match case");

    public static final int AMOUNT = values().length;

    private final String errorName;

    NodeType(String errorName) {
        this.errorName = errorName;
    }

    @Override
    public String ruleDefinitionName() {
        return name();
    }

    @Override
    public TokenType terminal() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NodeType nonterminal() {
        return this;
    }

    @Override
    public String errorName() {
        return errorName;
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public String toString() {
        return ruleDefinitionName();
    }

    @Override
    public int index() {
        return ordinal();
    }
}
