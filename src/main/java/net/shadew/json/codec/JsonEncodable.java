package net.shadew.json.codec;

import net.shadew.json.JsonNode;

/**
 * A generic object that can be encoded and decoded from JSON. If your object is a simple, mutable object, this can be
 * ideal instead of defining the encoding logic in a codec. A codec can then be created via {@link
 * JsonCodec#ofEncodable}.
 * <p>
 * Note: do not implement this interface if your class is more complicated or not/partially mutable.
 *
 * @see JsonRepresentable
 */
public interface JsonEncodable extends JsonRepresentable {
    /**
     * Encodes this object as a JSON structure.
     *
     * @return The encoded object
     */
    @Override
    JsonNode toJson();

    /**
     * Decodes a JSON tree and changes the state of this object to match the JSON tree.
     *
     * @param json The JSON tree to decode
     */
    void fromJson(JsonNode json);
}
