package net.shadew.json.template.parser;

import org.junit.jupiter.api.Test;

import net.shadew.json.JsonNode;
import net.shadew.json.template.JsonTemplate;
import net.shadew.json.template.TemplateContext;
import net.shadew.json.template.TemplateDebug;

import static net.shadew.json.template.parser.DocumentNode.*;
import static net.shadew.json.template.parser.EntityNode.*;
import static net.shadew.json.template.parser.ExpressionNode.*;

public class NodeTests {
    @Test
    void test() {
        TemplateDebug.debug = true;
//        TemplateDebug.enterFrame = (exec, frame) -> {
//            System.out.println("- ENTER FRAME " + frame.name + " @" + exec.pos());
//        };
//        TemplateDebug.exitFrame = (exec, frame) -> {
//            System.out.println("- EXIT FRAME " + frame.name + " @" + exec.pos());
//        };

        TemplateContext context = new TemplateContext();
//        context.exceptionProcessor((type, problem) -> {
//            throw new RuntimeException(type + ": " + problem);
//        });

        context.vfl().set("hello", JsonNode.number(3));

        DocumentNode node = document(
            array(
                forFromToBlock(
                    "i", literal(0), literal(10),
                    switchBlock(variable("i").thenBAnd(literal(3)))
                        .appendCase(caseBlock(
                            literal(0),
                            literal("zero")
                        ))
                        .appendCase(caseBlock(
                            literal(1),
                            literal("one")
                        ))
                        .appendCase(caseBlock(
                            literal(2),
                            literal("two")
                        ))
                        .appendCase(caseBlock(
                            literal(3),
                            literal("three")
                        )),
                    ifBlock(
                        literal(false),
                        forFromToBlock(
                            "i", literal(0), literal(10),
                            breakStatement()
                        )
                    )
                )
            )
        );

        System.out.println(node.asString());
        node.updateTree(null);
        node.visit(ParseTreeVisitor.join(new LoopDepthVisitor(), new ParseTreePrinter(System.out, true)));

        JsonTemplate expr = node.compile();
        System.out.println(expr.evaluate(context));
    }
}
