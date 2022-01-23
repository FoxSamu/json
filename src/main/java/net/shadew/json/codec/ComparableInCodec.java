package net.shadew.json.codec;

import net.shadew.json.JsonNode;

class ComparableInCodec<A extends Comparable<A>> implements JsonCodec<A> {
    private final JsonCodec<A> codec;
    private final A min, max;

    ComparableInCodec(JsonCodec<A> codec, A min, A max) {
        this.codec = codec;
        this.min = min;
        this.max = max;

        if (max.compareTo(min) < 0)
            throw new IllegalArgumentException("max < min");
    }

    private A check(A a) {
        if (a.compareTo(min) < 0 || a.compareTo(max) > 0)
            throw new JsonCodecException("Value " + a + " out of range [" + min + ", " + max + "]");
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
