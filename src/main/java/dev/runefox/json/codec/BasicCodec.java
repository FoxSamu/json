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

import java.util.function.Function;

class BasicCodec<A> implements JsonCodec<A> {
    private final Function<A, JsonNode> encode;
    private final Function<JsonNode, A> decode;

    BasicCodec(Function<A, JsonNode> encode, Function<JsonNode, A> decode) {
        this.encode = encode;
        this.decode = decode;
    }

    @Override
    public JsonNode encode(A obj) {
        return encode.apply(obj);
    }

    @Override
    public A decode(JsonNode json) {
        return decode.apply(json);
    }
}
