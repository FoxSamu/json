package net.shadew.json.template.parser;

public interface ParseTreeVisitor {
    void enter(NodeType type, ParsedTemplateNode node);
    void exit(NodeType type, ParsedTemplateNode node);

    static ParseTreeVisitor join(ParseTreeVisitor... visitors) {
        return new ParseTreeVisitor() {
            @Override
            public void enter(NodeType type, ParsedTemplateNode node) {
                for (ParseTreeVisitor visitor : visitors)
                    visitor.enter(type, node);
            }

            @Override
            public void exit(NodeType type, ParsedTemplateNode node) {
                for (ParseTreeVisitor visitor : visitors)
                    visitor.exit(type, node);
            }
        };
    }
}
