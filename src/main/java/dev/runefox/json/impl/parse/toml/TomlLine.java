package dev.runefox.json.impl.parse.toml;

import java.util.List;

public class TomlLine {
    private final List<String> path;

    public TomlLine(List<String> path) {
        this.path = List.copyOf(path);
    }


    public enum Type {
        KEY_VALUE,
        TABLE,
        TABLE_ARRAY
    }
}
