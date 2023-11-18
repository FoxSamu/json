package dev.runefox.json.codec;

import dev.runefox.json.JsonNode;

import java.util.Comparator;

class ComparatorAboveCodec<A> implements JsonCodec<A> {
    private final JsonCodec<A> codec;
    private final A min;
    private final Comparator<? super A> comp;

    ComparatorAboveCodec(JsonCodec<A> codec, A min, Comparator<? super A> comp) {
        this.codec = codec;
        this.min = min;
        this.comp = comp;
    }

    private A check(A a) {
        if (comp.compare(a, min) < 0)
            throw new CodecException("Value " + a + " under minimum " + min + "");
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
