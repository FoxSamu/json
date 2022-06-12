package net.shadew.json.template.parser;

import java.util.function.UnaryOperator;

import net.shadew.json.JsonNode;
import net.shadew.json.template.Operators;

public enum UnaryOperatorType {
    PLUS("+", Operators::unaryPlus),
    MINUS("-", Operators::neg),
    NOT("!", Operators::not),
    BIT_NOT("~", Operators::bnot),
    LENGTH("#", Operators::len),
    COPY("copy", Operators::copy);

    public final String symbol;
    public final UnaryOperator<JsonNode> operator;

    UnaryOperatorType(String symbol, UnaryOperator<JsonNode> operator) {
        this.symbol = symbol;
        this.operator = operator;
    }
}
