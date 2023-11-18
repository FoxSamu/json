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
