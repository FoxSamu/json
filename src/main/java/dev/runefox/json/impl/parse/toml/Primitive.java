package dev.runefox.json.impl.parse.toml;

import dev.runefox.json.JsonNode;
import dev.runefox.json.impl.parse.Token;

public record Primitive(
    JsonNode node,
    int fromPos, int fromLine, int fromCol,
    int toPos, int toLine, int toCol
) implements TomlValue {
    public Primitive(JsonNode node, Token token) {
        this(node, token, token);
    }

    public Primitive(JsonNode node, Token fst, Token lst) {
        this(
            node,
            fst.fromPos(), fst.fromLine(), fst.fromCol(),
            lst.toPos(), lst.toLine(), lst.toCol()
        );
    }

    @Override
    public JsonNode toJson() {
        return node;
    }
}
