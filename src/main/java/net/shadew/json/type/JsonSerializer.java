package net.shadew.json.type;

import net.shadew.json.JsonNode;

public interface JsonSerializer<T> {
    T deserialize(JsonNode node);
    JsonNode serialize(T obj);
}
