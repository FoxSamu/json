package dev.runefox.json;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

class JsonArrayCollector<T> implements Collector<T, JsonNode, JsonNode> {
    private final Function<? super T, JsonNode> serializer;

    JsonArrayCollector(Function<? super T, JsonNode> serializer) {
        this.serializer = serializer;
    }

    @Override
    public Supplier<JsonNode> supplier() {
        return JsonNode::array;
    }

    @Override
    public BiConsumer<JsonNode, T> accumulator() {
        return (node, v) -> node.add(serializer.apply(v));
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
