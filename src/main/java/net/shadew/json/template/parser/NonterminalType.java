package net.shadew.json.template.parser;

public interface NonterminalType extends ParserNodeType {
    int AMOUNT = NodeType.AMOUNT + Nonterminal.AMOUNT;
    int index();
}
