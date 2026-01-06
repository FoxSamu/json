/*
 * Copyright 2022-2026 O. W. Nankman
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "
 * AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package dev.runefox.json.codec;

import dev.runefox.json.JsonNode;

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
