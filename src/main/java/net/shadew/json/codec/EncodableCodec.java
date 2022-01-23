package net.shadew.json.codec;

import java.util.function.Function;

import net.shadew.json.JsonNode;

class EncodableCodec<A extends JsonEncodable> implements JsonCodec<A> {
    private final Function<JsonNode, A> instanceFactory;

    EncodableCodec(Function<JsonNode, A> instanceFactory) {
        this.instanceFactory = instanceFactory;
    }

    @Override
    public JsonNode encode(A obj) {
        return obj.toJson();
    }

    @Override
    public A decode(JsonNode json) {
        A obj = instanceFactory.apply(json);
        obj.fromJson(json);
        return obj;
    }
}
