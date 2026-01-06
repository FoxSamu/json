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
