package dev.runefox.json.impl.parse.toml;

import dev.runefox.json.JsonNode;
import dev.runefox.json.SyntaxException;

import java.util.ArrayList;
import java.util.List;

public class DocumentTableArray implements TomlValue, DocumentTableBuilder {
    private final Header firstHeader;
    private Header currentHeader;

    private final List<Table> tables = new ArrayList<>();

    public DocumentTableArray(Header firstHeader) {
        this.firstHeader = firstHeader;
        this.currentHeader = firstHeader;

        this.tables.add(new Table());
    }

    @Override
    public Header firstHeader() {
        return firstHeader;
    }

    @Override
    public Header currentHeader() {
        return currentHeader;
    }

    @Override
    public Table currentTable() {
        return tables.get(tables.size() - 1);
    }

    @Override
    public void tableHeader(Header currentHeader) throws SyntaxException {
        throw currentHeader.error("Cannot create " + currentHeader.reportKey() + " because it's a table array");
    }

    @Override
    public void tableArrayHeader(Header currentHeader) {
        this.currentHeader = currentHeader;
        tables.add(new Table());
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
        return tables.stream().map(Table::toJson).collect(JsonNode.arrayCollector());
    }
}
