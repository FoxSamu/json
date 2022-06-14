package net.shadew.json.template.parser;

public enum AssignType implements Expectable {
    SET("=", null),
    ADD("+=", BinaryOperatorType.ADD),
    SUB("-=", BinaryOperatorType.SUB),
    MUL("*=", BinaryOperatorType.MUL),
    DIV("/=", BinaryOperatorType.DIV),
    MOD("%=", BinaryOperatorType.MOD),
    BIT_LSH("<<=", BinaryOperatorType.BIT_LSH),
    BIT_RSH(">>=", BinaryOperatorType.BIT_RSH),
    BIT_RRSH(">>>=", BinaryOperatorType.BIT_RRSH),
    BIT_AND("&=", BinaryOperatorType.BIT_AND),
    BIT_OR("|=", BinaryOperatorType.BIT_OR),
    BIT_XOR("^=", BinaryOperatorType.BIT_XOR);

    public final String symbol;
    public final BinaryOperatorType operator;

    AssignType(String symbol, BinaryOperatorType operator) {
        this.symbol = symbol;
        this.operator = operator;
    }

    @Override
    public String errorName() {
        return "'" + symbol + "'";
    }
}
