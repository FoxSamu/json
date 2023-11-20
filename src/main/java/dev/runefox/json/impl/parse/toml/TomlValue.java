package dev.runefox.json.impl.parse.toml;

import dev.runefox.json.JsonNode;

public interface TomlValue extends TomlSyntax {
    JsonNode toJson();
}
