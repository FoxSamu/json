package dev.runefox.json.codec;

import dev.runefox.json.JsonNode;

import java.util.function.Function;

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
