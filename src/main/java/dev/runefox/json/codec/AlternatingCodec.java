package dev.runefox.json.codec;

import dev.runefox.json.JsonNode;
import dev.runefox.json.NodeException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class AlternatingCodec<A> implements JsonCodec<A> {
    private final List<JsonCodec<A>> options;

    AlternatingCodec(List<JsonCodec<A>> options) {
        this.options = options;
    }

    @Override
    public JsonNode encode(A obj) {
        List<NodeException> exceptions = null;
        for (JsonCodec<A> codec : options) {
            try {
                return codec.encode(obj);
            } catch (NoCodecImplementation ignored) {
            } catch (NodeException exc) {
                if (exceptions == null)
                    exceptions = new ArrayList<>();
                exceptions.add(exc);
            }
        }
        if (exceptions == null)
            throw new CodecException("Could not encode");

        if (exceptions.size() == 1)
            throw exceptions.get(0);

        CodecException exc = new CodecException("Could not encode");
        exceptions.forEach(exc::addSuppressed);
        throw exc;
    }

    @Override
    public A decode(JsonNode json) {
        List<NodeException> exceptions = null;
        for (JsonCodec<A> codec : options) {
            try {
                return codec.decode(json);
            } catch (NoCodecImplementation ignored) {
            } catch (NodeException exc) {
                if (exceptions == null)
                    exceptions = new ArrayList<>();
                exceptions.add(exc);
            }
        }
        if (exceptions == null)
            throw new CodecException("Could not decode");

        if (exceptions.size() == 1)
            throw exceptions.get(0);

        CodecException exc = new CodecException("Could not decode");
        exceptions.forEach(exc::addSuppressed);
        throw exc;
    }

    @Override
    public JsonCodec<A> alternatively(JsonCodec<A> option) {
        return new AlternatingCodec<>(
            List.copyOf(Stream.concat(
                options.stream(),
                Stream.of(option)
            ).toList())
        );
    }
}
