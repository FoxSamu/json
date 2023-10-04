package dev.runefox.json.codec;

import dev.runefox.json.JsonNode;

import java.util.Comparator;

class ComparatorUnderCodec<A> implements JsonCodec<A> {
    private final JsonCodec<A> codec;
    private final A max;
    private final Comparator<? super A> comp;

    ComparatorUnderCodec(JsonCodec<A> codec, A max, Comparator<? super A> comp) {
        this.codec = codec;
        this.max = max;
        this.comp = comp;
    }

    private A check(A a) {
        if (comp.compare(a, max) > 0)
            throw new JsonCodecException("Value " + a + " above limit " + max + "");
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
