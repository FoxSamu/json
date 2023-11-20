package dev.runefox.json.impl.parse.toml;

import dev.runefox.json.JsonNode;
import dev.runefox.json.SyntaxException;

public class Document {
    private final Table rootTable = new Table();
    private Table currentTable = rootTable;



    public void apply(KeyValue pair) throws SyntaxException {
        currentTable.add(pair);
    }

    public void table(Header header) throws SyntaxException {

    }

    public void tableArray(Header header) throws SyntaxException {

    }

    public JsonNode toJson() throws SyntaxException {
        return rootTable.toJson();
    }
}
