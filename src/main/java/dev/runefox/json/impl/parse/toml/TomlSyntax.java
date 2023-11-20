package dev.runefox.json.impl.parse.toml;

import dev.runefox.json.SyntaxException;

public interface TomlSyntax {
    int fromPos();
    int fromLine();
    int fromCol();

    int toPos();
    int toLine();
    int toCol();

    default SyntaxException error(String message) {
        return new SyntaxException(fromPos(), fromLine(), fromCol(), toPos(), toLine(), toCol(), message);
    }
}
