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

package dev.runefox.json.impl;

import dev.runefox.json.JsonNode;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class JsonObjectCollector<T> implements Collector<T, JsonNode, JsonNode> {
    private final Function<? super T, String> namer;
    private final Function<? super T, JsonNode> serializer;

    public JsonObjectCollector(Function<? super T, String> namer, Function<? super T, JsonNode> serializer) {
        this.namer = namer;
        this.serializer = serializer;
    }

    @Override
    public Supplier<JsonNode> supplier() {
        return JsonNode::object;
    }

    @Override
    public BiConsumer<JsonNode, T> accumulator() {
        return (node, v) -> node.set(namer.apply(v), serializer.apply(v));
    }

    @Override
    public BinaryOperator<JsonNode> combiner() {
        return JsonNode::merge;
    }

    @Override
    public Function<JsonNode, JsonNode> finisher() {
        return Function.identity();
    }

    private static final Set<Characteristics> CHARACTERISTICS = Set.of(
        Characteristics.IDENTITY_FINISH
    );

    @Override
    public Set<Characteristics> characteristics() {
        return CHARACTERISTICS;
    }
}
