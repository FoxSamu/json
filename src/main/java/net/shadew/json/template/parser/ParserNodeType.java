package net.shadew.json.template.parser;

public interface ParserNodeType extends Expectable {
    boolean isTerminal();
    String ruleDefinitionName();

    TokenType terminal();
    NonterminalType nonterminal();
}
