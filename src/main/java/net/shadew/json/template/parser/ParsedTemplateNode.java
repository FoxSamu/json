package net.shadew.json.template.parser;

import java.util.List;

public abstract class ParsedTemplateNode {
    public abstract NodeType type();

    public abstract EntityNode asEntity();

    public List<ParsedTemplateNode> children() {
        return List.of();
    }

    public abstract String asString();

}
