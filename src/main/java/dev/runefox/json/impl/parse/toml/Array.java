package dev.runefox.json.impl.parse.toml;

import dev.runefox.json.JsonNode;
import dev.runefox.json.SyntaxException;
import dev.runefox.json.impl.parse.Token;

import java.util.ArrayList;
import java.util.List;

public class Array implements TomlValue {
    private final List<TomlValue> values = new ArrayList<>();

    private int fromPos, fromLine, fromCol;
    private int toPos, toLine, toCol;

    public void begin(Token token) {
        fromPos = token.fromPos();
        fromLine = token.fromLine();
        fromCol = token.fromCol();
    }

    public void end(Token token) {
        toPos = token.toPos();
        toLine = token.toLine();
        toCol = token.toCol();
    }

    public void add(TomlValue val) throws SyntaxException {
        values.add(val);
    }

    @Override
    public JsonNode toJson() {
        JsonNode arr = JsonNode.array();
        for (TomlValue val : values) {
            arr.add(val.toJson());
        }
        return arr;
    }

    @Override
    public int fromPos() {
        return fromPos;
    }

    @Override
    public int fromLine() {
        return fromLine;
    }

    @Override
    public int fromCol() {
        return fromCol;
    }

    @Override
    public int toPos() {
        return toPos;
    }

    @Override
    public int toLine() {
        return toLine;
    }

    @Override
    public int toCol() {
        return toCol;
    }
}
