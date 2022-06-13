package net.shadew.json.template.parser;

import java.io.PrintStream;

public class ParseTreePrinter implements ParseTreeVisitor {
    private final PrintStream out;
    private int indent;
    private final boolean properties;

    public ParseTreePrinter(PrintStream out, boolean properties) {
        this.out = out;
        this.properties = properties;
    }

    @Override
    public void enter(NodeType type, ParsedTemplateNode node) {
        out.println("    ".repeat(indent) + type + (node.children().size() > 0 || !node.properties().isEmpty() ? " {" : ""));
        indent++;

        if (properties) {
            node.properties().forEach((k, v) -> out.println("    ".repeat(indent) + "- " + k + ": " + v));
        }
    }

    @Override
    public void exit(NodeType type, ParsedTemplateNode node) {
        indent--;

        if (node.children().size() > 0 || !node.properties().isEmpty())
            out.println("    ".repeat(indent) + "}");
    }
}
