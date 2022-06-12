package net.shadew.json.template.parser;

import java.util.function.BinaryOperator;

import net.shadew.json.JsonNode;
import net.shadew.json.template.Operators;

public enum BinaryOperatorType {
    ADD("+", Operators::add),
    SUB("-", Operators::sub),
    MUL("*", Operators::mul),
    DIV("/", Operators::div),
    MOD("%", Operators::mod),
    BIT_LSH("<<", Operators::blsh),
    BIT_RSH(">>", Operators::brsh),
    BIT_RRSH(">>>", Operators::brrsh),
    BIT_AND("&", Operators::band),
    BIT_OR("|", Operators::bor),
    BIT_XOR("^", Operators::bxor),
    EQ("==", Operators::eq),
    NEQ("!=", Operators::neq),
    LT("<", Operators::lt),
    GT(">", Operators::gt),
    LE("<=", Operators::le),
    GE(">=", Operators::ge);

    public final String symbol;
    public final BinaryOperator<JsonNode> operator;

    BinaryOperatorType(String symbol, BinaryOperator<JsonNode> operator) {
        this.symbol = symbol;
        this.operator = operator;
    }
}
