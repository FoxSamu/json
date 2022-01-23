package net.shadew.json;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

enum JsonArrayCollector implements Collector<JsonNode, JsonNode, JsonNode> {
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
