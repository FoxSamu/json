package net.shadew.json;

/**
 * @deprecated In favour of the new type serializer API
 */
@Deprecated
public interface JsonSerializable {
    JsonNode toJsonTree();
}
