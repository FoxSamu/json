package net.shadew.json.template.parser;

public interface ParseTreeVisitor {
    void enter(NodeType type, ParserNode node);
    void exit(NodeType type, ParserNode node);

    static ParseTreeVisitor join(ParseTreeVisitor... visitors) {
        return new ParseTreeVisitor() {
            @Override
            public void enter(NodeType type, ParserNode node) {
                for (ParseTreeVisitor visitor : visitors)
                    visitor.enter(type, node);
            }

            @Override
            public void exit(NodeType type, ParserNode node) {
                for (ParseTreeVisitor visitor : visitors)
                    visitor.exit(type, node);
            }
        };
    }
}
