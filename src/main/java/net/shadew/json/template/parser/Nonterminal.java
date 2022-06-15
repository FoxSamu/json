package net.shadew.json.template.parser;

public interface Nonterminal extends GrammarSymbol {
    int AMOUNT = NodeType.AMOUNT + NonterminalType.AMOUNT;
    int index();
}
