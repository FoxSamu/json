package net.shadew.json.template.parser;

import org.junit.jupiter.api.Test;

import java.io.StringReader;

import net.shadew.json.JsonSyntaxException;
import net.shadew.json.template.TemplateDebug;

public class ParserTests {
    @Test
    void test() throws JsonSyntaxException {
        TemplateDebug.debug = true;

        TemplateLexer lexer = new TemplateLexer(new StringReader("0 + 1 * 2 + 3"));
        ParserNode node = TemplateParser.parse(lexer);

        System.out.println();
        System.out.println("Parsed!");
        System.out.println(node.type() + ": " + node.asString());
        System.out.println("Tree:");
        node.updateTree(null);
        node.visit(new ParseTreePrinter(System.out, false));
    }
}
