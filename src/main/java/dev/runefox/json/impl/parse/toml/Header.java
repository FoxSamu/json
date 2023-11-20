package dev.runefox.json.impl.parse.toml;

import dev.runefox.json.impl.parse.Token;

import java.util.List;

public record Header(
    List<String> key,
    int fromPos, int fromLine, int fromCol,
    int toPos, int toLine, int toCol
) implements TomlSyntax {
    public Header {
        key = List.copyOf(key);
    }

    public Header(List<String> key, Token start, Token end) {
        this(
            key,
            start.fromPos(), start.fromLine(), start.fromCol(),
            end.toPos(), end.toLine(), end.toCol()
        );
    }

    public String reportKey() {
        return KeyValue.reportKey(key);
    }
}
