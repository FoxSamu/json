package net.shadew.json.codec;

import java.util.function.Function;

import net.shadew.json.JsonNode;

class BasicCodec<A> implements JsonCodec<A> {
    private final Function<A, JsonNode> encode;
    private final Function<JsonNode, A> decode;

    BasicCodec(Function<A, JsonNode> encode, Function<JsonNode, A> decode) {
        this.encode = encode;
        this.decode = decode;
    }

    @Override
    public JsonNode encode(A obj) {
        return encode.apply(obj);
    }

    @Override
    public A decode(JsonNode json) {
        return decode.apply(json);
    }
}
