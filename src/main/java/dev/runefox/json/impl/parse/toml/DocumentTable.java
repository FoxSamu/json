package dev.runefox.json.impl.parse.toml;

import dev.runefox.json.JsonNode;
import dev.runefox.json.SyntaxException;

import java.util.List;

public class DocumentTable implements TomlValue, DocumentTableBuilder {
    private final Header firstHeader;
    private Header currentHeader;

    private final Table table = new Table();

    public DocumentTable(Header firstHeader) {
        this.firstHeader = firstHeader;
        this.currentHeader = firstHeader;
    }

    @Override
    public Header firstHeader() {
        return firstHeader;
    }

    @Override
    public Header currentHeader() {
        return firstHeader;
    }

    @Override
    public Table currentTable() {
        return table;
    }

    @Override
    public void tableHeader(Header header) throws SyntaxException {
        int matchLen = 0;
        List<String> cur = currentHeader.key();
        List<String> now = header.key();
        for (int i = 0, l = Math.min(cur.size(), now.size()); i < l; i++) {
            String c = cur.get(i);
            String h = now.get(i);

            if (!c.equals(h)) {
                matchLen = i;
                break;
            }
        }

        if (matchLen >= cur.size()) {
            throw header.error("Table " + header.reportKey() + " is already defined");
        }

        currentHeader = header;
    }

    @Override
    public void tableArrayHeader(Header header) throws SyntaxException {
        throw header.error("Cannot add table to " + header.reportKey() + " because it's not a table array");
    }

    @Override
    public int fromPos() {
        return currentHeader.fromPos();
    }

    @Override
    public int fromLine() {
        return currentHeader.fromLine();
    }

    @Override
    public int fromCol() {
        return currentHeader.fromCol();
    }

    @Override
    public int toPos() {
        return currentHeader.toPos();
    }

    @Override
    public int toLine() {
        return currentHeader.toLine();
    }

    @Override
    public int toCol() {
        return currentHeader.toCol();
    }

    @Override
    public JsonNode toJson() {
        return table.toJson();
    }
}
