package net.shadew.json.codec;

import net.shadew.json.JsonNode;

public interface JsonEncodable extends JsonRepresentable {
    @Override
    JsonNode toJson();

    void fromJson(JsonNode json);
}
