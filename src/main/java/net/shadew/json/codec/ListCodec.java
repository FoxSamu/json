package net.shadew.json.codec;

import java.util.List;
import java.util.stream.Collectors;

import net.shadew.json.JsonNode;

class ListCodec<A> implements JsonCodec<List<A>> {
    private final JsonCodec<A> elementCodec;
    private final int minL, maxL;

    ListCodec(JsonCodec<A> elementCodec) {
        this.elementCodec = elementCodec;
        this.minL = 0;
        this.maxL = Integer.MAX_VALUE;
    }

    ListCodec(JsonCodec<A> elementCodec, int maxL) {
        this.elementCodec = elementCodec;
        if (maxL < 0)
            throw new IllegalArgumentException("max < min");

        this.minL = 0;
        this.maxL = maxL;
    }

    ListCodec(JsonCodec<A> elementCodec, int minL, int maxL) {
        this.elementCodec = elementCodec;
        minL = Math.max(0, minL);
        if (maxL < minL)
            throw new IllegalArgumentException("max < min");

        this.minL = minL;
        this.maxL = maxL;
    }

    private void checkLength(int l) {
        if (l < minL || l > maxL) {
            throw new JsonCodecException("Length of list out of expected range [" + minL + ".." + maxL + "]");
        }
    }

    @Override
    public JsonNode encode(List<A> obj) {
        checkLength(obj.size());
        return obj.stream().map(elementCodec::encode).collect(JsonNode.arrayCollector());
    }

    @Override
    public List<A> decode(JsonNode json) {
        checkLength(json.requireArray().size());
        return json.stream().map(elementCodec::decode).collect(Collectors.toList());
    }
}
