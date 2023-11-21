package dev.runefox.json.impl.parse.toml;

import dev.runefox.json.SyntaxException;

public interface DocumentTableBuilder extends TomlValue {
    public Header firstHeader();
    public Header currentHeader();
    public Table currentTable();
    public void tableHeader(Header currentHeader) throws SyntaxException;
    public void tableArrayHeader(Header currentHeader) throws SyntaxException;
}
