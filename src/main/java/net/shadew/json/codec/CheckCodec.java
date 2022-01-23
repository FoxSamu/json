package net.shadew.json.codec;

import java.util.function.Function;
import java.util.function.Predicate;

import net.shadew.json.JsonNode;

class CheckCodec<A> implements JsonCodec<A> {
    private final JsonCodec<A> codec;
    private final Predicate<A> validator;
    private final Function<A, String> errorProvider;

    CheckCodec(JsonCodec<A> codec, Predicate<A> validator, Function<A, String> errorProvider) {
        this.codec = codec;
        this.validator = validator;
        this.errorProvider = errorProvider;
    }

    private A check(A obj) {
        if (!validator.test(obj))
            throw new JsonCodecException(errorProvider.apply(obj));
        return obj;
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
