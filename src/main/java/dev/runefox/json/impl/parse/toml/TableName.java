package dev.runefox.json.impl.parse.toml;

import java.util.List;

public record TableName(
    List<String> key, boolean array,
    int fromPos, int fromLine, int fromCol,
    int toPos, int toLine, int toCol
) implements TomlSyntax {
}
