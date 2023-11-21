package dev.runefox.json.impl.parse.toml;

import dev.runefox.json.JsonNode;
import dev.runefox.json.SyntaxException;

public class Document {
    private final Table rootTable = new Table();
    private DocumentTableBuilder currentDtb = null;

    private Table currentTable() {
        if (currentDtb != null) {
            return currentDtb.currentTable();
        }
        return rootTable;
    }

    private DocumentTableBuilder dtb(Header header, boolean array) throws SyntaxException {
        return rootTable.dtb(header, array);
    }


    public void apply(KeyValue pair) throws SyntaxException {
        currentTable().add(pair);
    }

    public void table(Header header) throws SyntaxException {
        currentDtb = dtb(header, false);
    }

    public void tableArray(Header header) throws SyntaxException {
        currentDtb = dtb(header, true);
    }

    public JsonNode toJson() throws SyntaxException {
        return rootTable.toJson();
    }
}
