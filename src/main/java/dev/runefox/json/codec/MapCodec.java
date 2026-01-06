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
import dev.runefox.json.NodeException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MapCodec<A, K> implements JsonCodec<Map<K, A>> {
    private final JsonCodec<A> valueCodec;
    private final Function<K, String> keyToString;
    private final Function<String, K> stringToKey;

    public MapCodec(JsonCodec<A> valueCodec, Function<K, String> keyToString, Function<String, K> stringToKey) {
        this.valueCodec = valueCodec;
        this.keyToString = keyToString;
        this.stringToKey = stringToKey;
    }

    @Override
    public JsonNode encode(Map<K, A> obj) {
        JsonNode json = JsonNode.object();
        obj.forEach((k, a) -> {
            String key = keyToString.apply(k);
            try {
                json.set(key, valueCodec.encode(a));
            } catch (NodeException exc) {
                throw new CodecException(key + " > " + exc.getMessage(), exc);
            }
        });
        return json;
    }

    @Override
    public Map<K, A> decode(JsonNode json) {
        Map<K, A> map = new HashMap<>();
        json.forEachEntry((key, a) -> {
            try {
                map.put(stringToKey.apply(key), valueCodec.decode(a));
            } catch (NodeException exc) {
                throw new CodecException(key + " > " + exc.getMessage(), exc);
            }
        });
        return map;
    }
}
