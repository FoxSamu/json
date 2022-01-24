package net.shadew.json.codec;

import net.shadew.json.JsonNode;

/**
 * A generic object that can be represented as a JSON tree. This does not mean it can be converted back to an object
 * easily. This method is solely for methods that require JSON input from either a raw {@link JsonNode} or a {@link
 * JsonEncodable}, or generally anything else that can represent itself as a JSON tree. As such, {@link JsonNode}
 * extends {@link JsonRepresentable}.
 */
public interface JsonRepresentable {
    /**
     * Converts this object to a JSON tree.
     *
     * @return The created JSON tree
     */
    JsonNode toJson();
}
