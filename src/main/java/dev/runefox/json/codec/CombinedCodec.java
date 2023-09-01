package dev.runefox.json.codec;

import dev.runefox.json.JsonException;
import dev.runefox.json.JsonNode;

import java.util.ArrayList;
import java.util.List;

class CombinedCodec<A> implements JsonCodec<A> {
    private final List<JsonCodec<A>> options;

    CombinedCodec(List<JsonCodec<A>> options) {
        this.options = options;
    }

    @Override
    public JsonNode encode(A obj) {
        List<JsonException> exceptions = null;
        for (JsonCodec<A> codec : options) {
            try {
                return codec.encode(obj);
            } catch (JsonException exc) {
                if (exceptions == null)
                    exceptions = new ArrayList<>();
                exceptions.add(exc);
            }
        }
        if (exceptions == null)
            throw new JsonCodecException("Could not encode");

        if (exceptions.size() == 1)
            throw exceptions.get(0);

        JsonCodecException exc = new JsonCodecException("Could not encode");
        exceptions.forEach(exc::addSuppressed);
        throw exc;
    }

    @Override
    public A decode(JsonNode json) {
        List<JsonException> exceptions = null;
        for (JsonCodec<A> codec : options) {
            try {
                return codec.decode(json);
            } catch (JsonException exc) {
                if (exceptions == null)
                    exceptions = new ArrayList<>();
                exceptions.add(exc);
            }
        }
        if (exceptions == null)
            throw new JsonCodecException("Could not decode");

        if (exceptions.size() == 1)
            throw exceptions.get(0);

        JsonCodecException exc = new JsonCodecException("Could not decode");
        exceptions.forEach(exc::addSuppressed);
        throw exc;
    }
}
