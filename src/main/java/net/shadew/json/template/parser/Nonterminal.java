package net.shadew.json.template.parser;

public enum Nonterminal implements NonterminalType {
    PARSER_START("parser start"),
    TOKEN("token"),
    EXPRESSION_PROD("expression"),
    EXPRESSION_SUM("expression sum"),
    EXPRESSION_BASE("expression base"),
    ENTITY("entity");

    public static final int AMOUNT = values().length;

    private final String errorName;

    Nonterminal(String errorName) {
        this.errorName = errorName;
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
    public String ruleDefinitionName() {
        return name();
    }

    @Override
    public TokenType terminal() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NonterminalType nonterminal() {
        return this;
    }

    @Override
    public int index() {
        return NodeType.AMOUNT + ordinal();
    }
}
