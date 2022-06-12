package net.shadew.json.template.parser;

import org.junit.jupiter.api.Test;

import net.shadew.json.JsonNode;
import net.shadew.json.template.JsonTemplate;
import net.shadew.json.template.TemplateContext;

import static net.shadew.json.template.parser.DocumentNode.*;
import static net.shadew.json.template.parser.EntityNode.*;
import static net.shadew.json.template.parser.ExpressionNode.*;

public class NodeTests {
    @Test
    void test() {
        TemplateContext context = new TemplateContext();

        context.vfl().set("hello", JsonNode.number(3));

        DocumentNode node = document(
            variable("var").assign(literal(JsonNode.number(9))).asVoidLine(),
            array(
                literal("hello"),
                ifBlock(
                    variable("var").thenEq(literal(3)),
                    literal("var is three")
                ).addElseIf(elseIfBlock(
                    variable("var").thenEq(literal(5)),
                    literal("var is five")
                )).addElse(elseBlock(
                    string("var is ").append(variable("var"))
                )),
                ifBlock(
                    literal(true),
                    voidLine(variable("var").assign(literal(5))),
                    voidLine(variable("newvar").assign(literal(2))),
                    object(
                        variable("var").withKey(literal("var")),
                        variable("newvar").withKey(literal("newvar"))
                    )
                ),
                object(
                    variable("var").withKey(literal("var")),
                    variable("newvar").withKey(literal("newvar"))
                )
            ),
            literal(JsonNode.NULL)
        );

        System.out.println(node.asString());
        JsonTemplate expr = node.compile();
        System.out.println(expr.evaluate(context));
    }
}
