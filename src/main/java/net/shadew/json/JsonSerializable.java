package net.shadew.json;

import net.shadew.json.codec.JsonCodec;
import net.shadew.json.codec.JsonEncodable;
import net.shadew.json.codec.JsonRepresentable;

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
