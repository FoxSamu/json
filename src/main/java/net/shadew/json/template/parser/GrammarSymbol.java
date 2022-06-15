package net.shadew.json.template.parser;

public interface GrammarSymbol extends Expectable {
    boolean isTerminal();
    String ruleDefinitionName();

    TokenType terminal();
    Nonterminal nonterminal();
}
