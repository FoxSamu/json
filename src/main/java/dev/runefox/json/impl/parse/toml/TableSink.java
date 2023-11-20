package dev.runefox.json.impl.parse.toml;

import dev.runefox.json.JsonNode;
import dev.runefox.json.SyntaxException;

public interface TableSink {
    void add(KeyValue pair) throws SyntaxException;
    JsonNode toJson() throws SyntaxException;
}
