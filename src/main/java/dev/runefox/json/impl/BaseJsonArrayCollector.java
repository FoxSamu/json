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

public enum BaseJsonArrayCollector implements Collector<JsonNode, JsonNode, JsonNode> {
    INSTANCE;

    @Override
    public Supplier<JsonNode> supplier() {
        return JsonNode::array;
    }

    @Override
    public BiConsumer<JsonNode, JsonNode> accumulator() {
        return JsonNode::add;
    }

    @Override
    public BinaryOperator<JsonNode> combiner() {
        return JsonNode::append;
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
