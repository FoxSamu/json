package net.shadew.json.codec;

import java.util.function.Function;

import net.shadew.json.JsonNode;

class MappedCodec<A, N> implements JsonCodec<A> {
    private final JsonCodec<N> codec;
    private final Function<A, N> unmap;
    private final Function<N, A> map;

    MappedCodec(JsonCodec<N> codec, Function<N, A> map, Function<A, N> unmap) {
        this.codec = codec;
        this.unmap = unmap;
        this.map = map;
    }

    @Override
    public JsonNode encode(A obj) {
        return codec.encode(unmap.apply(obj));
    }

    @Override
    public A decode(JsonNode json) {
        return map.apply(codec.decode(json));
    }
}
