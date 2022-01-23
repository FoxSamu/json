package net.shadew.json.codec;

import net.shadew.json.JsonNode;

class ComparableAboveCodec<A extends Comparable<A>> implements JsonCodec<A> {
    private final JsonCodec<A> codec;
    private final A min;

    ComparableAboveCodec(JsonCodec<A> codec, A min) {
        this.codec = codec;
        this.min = min;
    }

    private A check(A a) {
        if (a.compareTo(min) < 0)
            throw new JsonCodecException("Value " + a + " under minimum " + min + "");
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
