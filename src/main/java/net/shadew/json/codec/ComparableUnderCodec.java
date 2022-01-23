package net.shadew.json.codec;

import net.shadew.json.JsonNode;

class ComparableUnderCodec<A extends Comparable<A>> implements JsonCodec<A> {
    private final JsonCodec<A> codec;
    private final A max;

    ComparableUnderCodec(JsonCodec<A> codec, A max) {
        this.codec = codec;
        this.max = max;
    }

    private A check(A a) {
        if (a.compareTo(max) > 0)
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
