package net.shadew.json.codec;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.shadew.json.JsonException;
import net.shadew.json.JsonNode;

/**
 * A codec that encodes or decodes a record. This codec class automatically manages the checks and exceptions thrown by
 * inner codecs, and ensures required fields are present.
 * <p>
 * In terms of JSON codecs, a record is a flat JSON object with some properties. Flat means that all fields of the
 * encoded object have a separate property in the encoded JSON, and are not nested into inner structures if they are
 * neither in the Java object. For example, a person object with a first name and a last name as separate fields will
 * become {@code {"firstName": ..., "lastName": ...}}, but a person object with one name field of a name record will
 * encode like {@code {"name": {"first": ..., "last": ...}}}.
 * <p>
 * A {@link RecordCodec} will always require a JSON object, not an array or primitive. It will not encode null values.
 */
public abstract class RecordCodec<A> implements JsonCodec<A> {
    private final ThreadLocal<ContextStack<DecodeContextImpl<A>>> decodeStack
        = ThreadLocal.withInitial(() -> new ContextStack<>(DecodeContextImpl::new));

    private final ThreadLocal<ContextStack<EncodeContextImpl<A>>> encodeStack
        = ThreadLocal.withInitial(() -> new ContextStack<>(EncodeContextImpl::new));

    /**
     * {@inheritDoc}
     */
    @Override
    public A decode(JsonNode json) {
        json.requireObject();

        ContextStack<DecodeContextImpl<A>> stack = decodeStack.get();
        try {
            DecodeContextImpl<A> context = stack.push();
            context.json(json);
            return decode(context);
        } finally {
            stack.pop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonNode encode(A obj) {
        ContextStack<EncodeContextImpl<A>> stack = encodeStack.get();

        try {
            EncodeContextImpl<A> context = stack.push();
            context.newJson();
            encode(context, obj);
            return context.json;
        } finally {
            stack.pop();
        }
    }

    /**
     * Decodes the record.
     *
     * @param ctx The decode context, which wraps the JSON tree and checks the presence of required fields when reading
     *            them. This context is recycled after decoding, do not try to cache it.
     * @return The decoded object
     */
    protected abstract A decode(DecodeContext<A> ctx);

    /**
     * Encodes the record.
     *
     * @param ctx The encode context, which wraps the JSON tree and ensures fields are encoded the right way. This
     *            context is recycled after encoding, do not try to cache it.
     * @param obj The object to encode
     */
    protected abstract void encode(EncodeContext<A> ctx, A obj);

    private static class ContextStack<C> {
        private final List<C> stack = new ArrayList<>();
        private final Supplier<C> factory;
        private int pointer;

        private ContextStack(Supplier<C> factory) {
            this.factory = factory;
            stack.add(factory.get());
        }

        public C push() {
            if (pointer == stack.size()) {
                stack.add(factory.get());
            }

            return stack.get(pointer++);
        }

        public void pop() {
            if (pointer == 0)
                throw new EmptyStackException();
            pointer--;
        }
    }

    private static class DecodeContextImpl<I> implements DecodeContext<I> {
        private JsonNode json;

        void json(JsonNode json) {
            this.json = json;
        }

        @Override
        public JsonNode json() {
            return json;
        }

        @Override
        public boolean has(String key) {
            return json.has(key);
        }

        @Override
        public JsonNode field(String key) {
            if (!json.has(key))
                throw new JsonCodecException("Expected key '" + key + "'");
            return json.get(key);
        }

        @Override
        public JsonNode optionalField(String key) {
            return json.get(key);
        }

        @Override
        public void applyField(String key, Consumer<? super JsonNode> applier) {
            if (json.has(key)) {
                applier.accept(json.get(key));
            }
        }

        private static <A> A decode(JsonCodec<A> codec, JsonNode node, String key) {
            try {
                return codec.decode(node);
            } catch (JsonException exc) {
                throw new JsonCodecException(key + " > " + exc.getMessage(), exc);
            }
        }

        @Override
        public <A> A field(String key, JsonCodec<A> codec) {
            return decode(codec, field(key), key);
        }

        @Override
        public <A> A optionalField(String key, JsonCodec<A> codec) {
            JsonNode node = optionalField(key);
            return node == null ? null : decode(codec, node, key);
        }

        @Override
        public <A> void applyField(String key, JsonCodec<A> codec, Consumer<? super A> applier) {
            applyField(key, json -> applier.accept(decode(codec, json, key)));
        }

        private static <A extends JsonEncodable> A decode(Function<JsonNode, A> factory, JsonNode node, String key) {
            try {
                A obj = factory.apply(node);
                obj.fromJson(node);
                return obj;
            } catch (JsonException exc) {
                throw new JsonCodecException(key + " > " + exc.getMessage(), exc);
            }
        }

        @Override
        public <A extends JsonEncodable> A field(String key, Function<JsonNode, A> factory) {
            return decode(factory, field(key), key);
        }

        @Override
        public <A extends JsonEncodable> A optionalField(String key, Function<JsonNode, A> factory) {
            JsonNode node = optionalField(key);
            return node == null ? null : decode(factory, node, key);
        }

        @Override
        public <A extends JsonEncodable> void applyField(String key, Function<JsonNode, A> factory, Consumer<? super A> applier) {
            applyField(key, node -> applier.accept(decode(factory, node, key)));
        }

        @Override
        public <A extends JsonEncodable> A field(String key, Supplier<A> factory) {
            return field(key, node -> factory.get());
        }

        @Override
        public <A extends JsonEncodable> A optionalField(String key, Supplier<A> factory) {
            return optionalField(key, node -> factory.get());
        }

        @Override
        public <A extends JsonEncodable> void applyField(String key, Supplier<A> factory, Consumer<? super A> applier) {
            applyField(key, node -> factory.get(), applier);
        }
    }

    private static class EncodeContextImpl<I> implements EncodeContext<I> {
        private JsonNode json;

        void newJson() {
            json = JsonNode.object();
        }

        @Override
        public JsonNode json() {
            return json;
        }

        private static <A extends JsonRepresentable> JsonNode encode(A obj, String key) {
            try {
                return obj.toJson();
            } catch (JsonException exc) {
                throw new JsonCodecException(key + " > " + exc.getMessage(), exc);
            }
        }

        @Override
        public void field(String key, JsonRepresentable value) {
            json.set(key, value.toJson());
        }

        @Override
        public void supplyField(String key, Supplier<? extends JsonRepresentable> value) {
            field(key, encode(value.get(), key));
        }

        @Override
        public final void arrayField(String key, JsonRepresentable... value) {
            arrayField(key, Stream.of(value));
        }

        @Override
        public void arrayField(String key, Stream<? extends JsonRepresentable> value) {
            field(key, value.map(a -> encode(a, key)).collect(JsonNode.arrayCollector()));
        }

        @Override
        public void supplyArrayField(String key, Supplier<Stream<? extends JsonRepresentable>> value) {
            arrayField(key, value.get());
        }

        private static <A> JsonNode encode(JsonCodec<A> codec, A obj, String key) {
            try {
                return codec.encode(obj);
            } catch (JsonException exc) {
                throw new JsonCodecException(key + " > " + exc.getMessage(), exc);
            }
        }

        @Override
        public <A> void field(String key, JsonCodec<A> codec, A value) {
            field(key, encode(codec, value, key));
        }

        @Override
        public <A> void supplyField(String key, JsonCodec<A> codec, Supplier<? extends A> value) {
            field(key, encode(codec, value.get(), key));
        }

        @SafeVarargs
        @Override
        public final <A> void arrayField(String key, JsonCodec<A> codec, A... value) {
            arrayField(key, codec, Stream.of(value));
        }

        @Override
        public <A> void arrayField(String key, JsonCodec<A> codec, Stream<A> value) {
            field(key, value.map(a -> encode(codec, a, key)).collect(JsonNode.arrayCollector()));
        }

        @Override
        public <A> void supplyArrayField(String key, JsonCodec<A> codec, Supplier<Stream<A>> value) {
            arrayField(key, codec, value.get());
        }
    }

    /**
     * A decode context, which wraps a JSON tree for decoding
     */
    public interface DecodeContext<I> {
        /**
         * Returns the JSON tree to decode
         */
        JsonNode json();

        /**
         * Checks whether a specific key is present in the record JSON
         *
         * @param key The key to check
         * @return True if it is present, false if not
         */
        boolean has(String key);

        /**
         * Get the value of a required field, failing if the field is not present.
         *
         * @param key The name of the field
         * @return The field value, as raw JSON data
         */
        JsonNode field(String key);

        /**
         * Get the value of an optional field, returning null if the field is not present.
         *
         * @param key The name of the field
         * @return The field value, as raw JSON data, or null if absent
         */
        JsonNode optionalField(String key);

        /**
         * Get the value of an optional field, passing it on to the given function if the field is present.
         *
         * @param key     The name of the field
         * @param applier The function that applies the field
         */
        void applyField(String key, Consumer<? super JsonNode> applier);

        /**
         * Get the value of a required field, failing if the field is not present.
         *
         * @param key   The name of the field
         * @param codec The codec of the field's type
         * @return The field value, decoded by the given codec
         */
        <A> A field(String key, JsonCodec<A> codec);

        /**
         * Get the value of an optional field, returning null if the field is not present.
         *
         * @param key   The name of the field
         * @param codec The codec of the field's type
         * @return The field value, decoded by the given codec, or null if absent
         */
        <A> A optionalField(String key, JsonCodec<A> codec);

        /**
         * Get the value of an optional field, passing it on to the given function if the field is present.
         *
         * @param key     The name of the field
         * @param codec   The codec of the field's type
         * @param applier The function that applies the field
         */
        <A> void applyField(String key, JsonCodec<A> codec, Consumer<? super A> applier);

        /**
         * Get the value of a required field, failing if the field is not present.
         *
         * @param key     The name of the field
         * @param factory The factory of the {@link JsonEncodable} object
         * @return The field value, decoded
         */
        <A extends JsonEncodable> A field(String key, Function<JsonNode, A> factory);

        /**
         * Get the value of an optional field, returning null if the field is not present.
         *
         * @param key     The name of the field
         * @param factory The factory of the {@link JsonEncodable} object
         * @return The field value, decoded, or null if absent
         */
        <A extends JsonEncodable> A optionalField(String key, Function<JsonNode, A> factory);

        /**
         * Get the value of an optional field, passing it on to the given function if the field is present.
         *
         * @param key     The name of the field
         * @param factory The factory of the {@link JsonEncodable} object
         * @param applier The function that applies the field
         */
        <A extends JsonEncodable> void applyField(String key, Function<JsonNode, A> factory, Consumer<? super A> applier);

        /**
         * Get the value of a required field, failing if the field is not present.
         *
         * @param key     The name of the field
         * @param factory The factory of the {@link JsonEncodable} object
         * @return The field value, decoded
         */
        <A extends JsonEncodable> A field(String key, Supplier<A> factory);

        /**
         * Get the value of an optional field, returning null if the field is not present.
         *
         * @param key     The name of the field
         * @param factory The factory of the {@link JsonEncodable} object
         * @return The field value, decoded, or null if absent
         */
        <A extends JsonEncodable> A optionalField(String key, Supplier<A> factory);

        /**
         * Get the value of an optional field, passing it on to the given function if the field is present.
         *
         * @param key     The name of the field
         * @param factory The factory of the {@link JsonEncodable} object
         * @param applier The function that applies the field
         */
        <A extends JsonEncodable> void applyField(String key, Supplier<A> factory, Consumer<? super A> applier);
    }

    /**
     * An encode context, which wraps a JSON tree for encoding.
     */
    @SuppressWarnings("unchecked")
    public interface EncodeContext<I> {
        /**
         * Returns the JSON tree that is being encoded to.
         */
        JsonNode json();

        /**
         * Sets a field.
         *
         * @param key   The name of the field
         * @param value The value of the field
         */
        void field(String key, JsonRepresentable value);

        /**
         * Sets a field.
         *
         * @param key   The name of the field
         * @param value The field getter
         */
        void supplyField(String key, Supplier<? extends JsonRepresentable> value);

        /**
         * Sets an array field (as a JSON array).
         *
         * @param key   The name of the field
         * @param value The values of the array field
         */
        void arrayField(String key, JsonRepresentable... value);

        /**
         * Sets an array field (as a JSON array).
         *
         * @param key   The name of the field
         * @param value The values of the array field
         */
        void arrayField(String key, Stream<? extends JsonRepresentable> value);

        /**
         * Sets an array field (as a JSON array).
         *
         * @param key   The name of the field
         * @param value The values of the array field
         */
        void supplyArrayField(String key, Supplier<Stream<? extends JsonRepresentable>> value);

        /**
         * Sets a field.
         *
         * @param key   The name of the field
         * @param codec The codec of the value
         * @param value The value of the field
         */
        <A> void field(String key, JsonCodec<A> codec, A value);

        /**
         * Sets a field.
         *
         * @param key   The name of the field
         * @param codec The codec of the value
         * @param value The field getter
         */
        <A> void supplyField(String key, JsonCodec<A> codec, Supplier<? extends A> value);

        /**
         * Sets an array field (as a JSON array).
         *
         * @param key   The name of the field
         * @param codec The codec of the value
         * @param value The values of the array field
         */
        <A> void arrayField(String key, JsonCodec<A> codec, A... value);

        /**
         * Sets an array field (as a JSON array).
         *
         * @param key   The name of the field
         * @param codec The codec of the value
         * @param value The values of the array field
         */
        <A> void arrayField(String key, JsonCodec<A> codec, Stream<A> value);

        /**
         * Sets an array field (as a JSON array).
         *
         * @param key   The name of the field
         * @param codec The codec of the value
         * @param value The values of the array field
         */
        <A> void supplyArrayField(String key, JsonCodec<A> codec, Supplier<Stream<A>> value);
    }
}
