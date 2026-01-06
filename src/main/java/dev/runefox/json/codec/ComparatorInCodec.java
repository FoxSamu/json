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

import java.util.Comparator;

class ComparatorInCodec<A> implements JsonCodec<A> {
    private final JsonCodec<A> codec;
    private final A min, max;
    private final Comparator<? super A> comp;

    ComparatorInCodec(JsonCodec<A> codec, A min, A max, Comparator<? super A> comp) {
        this.codec = codec;
        this.min = min;
        this.max = max;
        this.comp = comp;

        if (comp.compare(max, min) < 0)
            throw new IllegalArgumentException("max < min");
    }

    private A check(A a) {
        if (comp.compare(a, min) < 0 || comp.compare(a, max) > 0)
            throw new CodecException("Value " + a + " out of range [" + min + ", " + max + "]");
        return a;
    }

    @Override
    public JsonNode encode(A obj) {
        return codec.encode(check(obj));
    }

    @Override
    public A decode(JsonNode json) {
        return check(codec.decode(json));
    }
}
