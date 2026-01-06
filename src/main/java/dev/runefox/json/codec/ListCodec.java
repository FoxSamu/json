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

import java.util.List;
import java.util.stream.Collectors;

class ListCodec<A> implements JsonCodec<List<A>> {
    private final JsonCodec<A> elementCodec;
    private final int minL, maxL;

    ListCodec(JsonCodec<A> elementCodec) {
        this.elementCodec = elementCodec;
        this.minL = 0;
        this.maxL = Integer.MAX_VALUE;
    }

    ListCodec(JsonCodec<A> elementCodec, int maxL) {
        this.elementCodec = elementCodec;
        if (maxL < 0)
            throw new IllegalArgumentException("max < min");

        this.minL = 0;
        this.maxL = maxL;
    }

    ListCodec(JsonCodec<A> elementCodec, int minL, int maxL) {
        this.elementCodec = elementCodec;
        minL = Math.max(0, minL);
        if (maxL < minL)
            throw new IllegalArgumentException("max < min");

        this.minL = minL;
        this.maxL = maxL;
    }

    private void checkLength(int l) {
        if (l < minL || l > maxL) {
            throw new CodecException("Length of list out of expected range [" + minL + ".." + maxL + "]");
        }
    }

    @Override
    public JsonNode encode(List<A> obj) {
        checkLength(obj.size());
        return obj.stream().map(elementCodec::encode).collect(JsonNode.arrayCollector());
    }

    @Override
    public List<A> decode(JsonNode json) {
        checkLength(json.requireArray().size());
        return json.stream().map(elementCodec::decode).collect(Collectors.toList());
    }
}
