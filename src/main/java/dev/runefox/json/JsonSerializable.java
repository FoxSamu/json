package dev.runefox.json;

import dev.runefox.json.codec.JsonCodec;
import dev.runefox.json.codec.JsonEncodable;
import dev.runefox.json.codec.JsonRepresentable;

/**
 * @deprecated This interface is planned for removal in a future version, in favour of the new codec system. The new
 *     equivalent is {@link JsonRepresentable}, but you preferably want to use {@link JsonEncodable} or {@link
 *     JsonCodec} instead.
 */
@Deprecated
public interface JsonSerializable extends JsonRepresentable {
    /**
     * Converts this object to a JSON tree.
     *
     * @deprecated Planned for removal. Implement {@link JsonRepresentable#toJson} instead.
     */
    @Deprecated
    JsonNode toJsonTree();

    @Override
    default JsonNode toJson() {
        return toJsonTree();
    }
}
