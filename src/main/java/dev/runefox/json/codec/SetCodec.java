package dev.runefox.json.codec;

import dev.runefox.json.JsonNode;

import java.util.Set;
import java.util.stream.Collectors;

class SetCodec<A> implements JsonCodec<Set<A>> {
    private final JsonCodec<A> elementCodec;

    SetCodec(JsonCodec<A> elementCodec) {
        this.elementCodec = elementCodec;
    }

    @Override
    public JsonNode encode(Set<A> obj) {
        return obj.stream().map(elementCodec::encode).collect(JsonNode.arrayCollector());
    }

    @Override
    public Set<A> decode(JsonNode json) {
        return json.stream().map(elementCodec::decode).collect(Collectors.toSet());
    }
}
