package net.shadew.json.template.parser;

import static net.shadew.json.template.parser.Grammar.*;

public class ParserDefinition {
    private static Grammar grammar;
    private static ParserTable table;

    public static Grammar grammar() {
        if (grammar == null) initGrammar();
        return grammar;
    }

    private static void initGrammar() {
        Builder builder = new Builder();

        builder.rule(
            rule(NodeType.ENT_VALUE, "EXPRESSION_SUM")
                .reduce1(n -> EntityNode.value(n.as(ExpressionNode.class))),

            rule(NonterminalType.EXPRESSION_SUM, "EXPRESSION_SUM '+' EXPRESSION_PROD")
                .reduce3((lhs, plus, rhs) -> ExpressionNode.binaryAdd(lhs.asExpr(), rhs.asExpr())),

            rule(NonterminalType.EXPRESSION_SUM, "EXPRESSION_SUM '-' EXPRESSION_PROD")
                .reduce3((lhs, plus, rhs) -> ExpressionNode.binarySub(lhs.asExpr(), rhs.asExpr())),

            rule(NonterminalType.EXPRESSION_SUM, "EXPRESSION_PROD")
                .reduce1(ParserNode::asExpr),

            rule(NonterminalType.EXPRESSION_PROD, "EXPRESSION_PROD '*' EXPRESSION_BASE")
                .reduce3((lhs, plus, rhs) -> ExpressionNode.binaryMul(lhs.asExpr(), rhs.asExpr())),

            rule(NonterminalType.EXPRESSION_PROD, "EXPRESSION_PROD '/' EXPRESSION_BASE")
                .reduce3((lhs, plus, rhs) -> ExpressionNode.binaryDiv(lhs.asExpr(), rhs.asExpr())),

            rule(NonterminalType.EXPRESSION_PROD, "EXPRESSION_BASE")
                .reduce1(ParserNode::asExpr),

            rule(NonterminalType.EXPRESSION_BASE, "EXPR_LITERAL")
                .reduce1(ParserNode::asExpr),

            rule(NonterminalType.EXPRESSION_BASE, "EXPR_VARIABLE")
                .reduce1(ParserNode::asExpr),

            rule(NodeType.EXPR_LITERAL, "[num]")
                .reduce1(n -> ExpressionNode.literal(n.asToken().value(Number.class))),

            rule(NodeType.EXPR_VARIABLE, "[id]")
                .reduce1(n -> ExpressionNode.variable(n.asToken().value(String.class)))
        );

        grammar = builder.build(NodeType.ENT_VALUE);
    }

    public static ParserTable table() {
        if (table == null) {
            table = ParserTable.generate(grammar());
        }
        return table;
    }
}
