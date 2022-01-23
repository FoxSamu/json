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

public abstract class RecordCodec<A> implements JsonCodec<A> {
    private final ThreadLocal<ContextStack<DecodeContextImpl<A>>> decodeStack
        = ThreadLocal.withInitial(() -> new ContextStack<>(DecodeContextImpl::new));

    private final ThreadLocal<ContextStack<EncodeContextImpl<A>>> encodeStack
        = ThreadLocal.withInitial(() -> new ContextStack<>(EncodeContextImpl::new));

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

    protected abstract A decode(DecodeContext<A> ctx);
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
        public void field(String key, JsonRepresentable obj) {
            json.set(key, obj.toJson());
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
        public <A> void field(String key, JsonCodec<A> codec, A obj) {
            field(key, encode(codec, obj, key));
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

    public interface DecodeContext<I> {
        JsonNode json();

        boolean has(String key);

        JsonNode field(String key);
        JsonNode optionalField(String key);
        void applyField(String key, Consumer<? super JsonNode> applier);

        <A> A field(String key, JsonCodec<A> codec);
        <A> A optionalField(String key, JsonCodec<A> codec);
        <A> void applyField(String key, JsonCodec<A> codec, Consumer<? super A> applier);

        <A extends JsonEncodable> A field(String key, Function<JsonNode, A> factory);
        <A extends JsonEncodable> A optionalField(String key, Function<JsonNode, A> factory);
        <A extends JsonEncodable> void applyField(String key, Function<JsonNode, A> factory, Consumer<? super A> applier);

        <A extends JsonEncodable> A field(String key, Supplier<A> factory);
        <A extends JsonEncodable> A optionalField(String key, Supplier<A> factory);
        <A extends JsonEncodable> void applyField(String key, Supplier<A> factory, Consumer<? super A> applier);
    }

    @SuppressWarnings("unchecked")
    public interface EncodeContext<I> {
        JsonNode json();

        void field(String key, JsonRepresentable obj);
        void supplyField(String key, Supplier<? extends JsonRepresentable> value);
        void arrayField(String key, JsonRepresentable... value);
        void arrayField(String key, Stream<? extends JsonRepresentable> value);
        void supplyArrayField(String key, Supplier<Stream<? extends JsonRepresentable>> value);

        <A> void field(String key, JsonCodec<A> codec, A obj);
        <A> void supplyField(String key, JsonCodec<A> codec, Supplier<? extends A> value);
        <A> void arrayField(String key, JsonCodec<A> codec, A... value);
        <A> void arrayField(String key, JsonCodec<A> codec, Stream<A> value);
        <A> void supplyArrayField(String key, JsonCodec<A> codec, Supplier<Stream<A>> value);
    }
}
