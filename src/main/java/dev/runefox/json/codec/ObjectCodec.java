package dev.runefox.json.codec;

import dev.runefox.json.JsonNode;
import dev.runefox.json.MissingKeyException;

import java.util.function.Function;
import java.util.function.Supplier;

interface ObjectCodec<T> extends JsonCodec<T> {
    void encodeObj(T obj, JsonNode json);
    T decodeObj(JsonNode json);

    @Override
    default JsonNode encode(T obj) {
        JsonNode node = JsonNode.object();
        encodeObj(obj, node);
        return node;
    }

    @Override
    default T decode(JsonNode json) {
        return decodeObj(json.requireObject());
    }

    record Field<T, P>(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter) {
        void apply(T obj, JsonNode json) {
            P val = getter.apply(obj);
            if (def != null && val == null)
                return;

            json.set(name, codec.encode(val));
        }

        P get(JsonNode obj) {
            if (!obj.has(name)) {
                if (def == null)
                    throw new MissingKeyException(name);
                return def.get();
            }

            return codec.decode(obj.get(name));
        }
    }

    static class Codec0<T> implements ObjectCodec<T> {
        private final ObjectCodecBuilder.Function0<T> ctor;

        Codec0(ObjectCodecBuilder.Function0<T> ctor) {
            this.ctor = ctor;
        }

        @Override
        public void encodeObj(T obj, JsonNode json) {
        }

        @Override
        public T decodeObj(JsonNode json) {
            return ctor.apply();
        }

        static class Builder<T> implements ObjectCodecBuilder.Builder0<T> {

            Builder() {
            }

            @Override
            public JsonCodec<T> build(ObjectCodecBuilder.Function0<T> ctor) {
                return new Codec0<>(ctor);
            }

            @Override
            public <P> ObjectCodecBuilder.Builder1<P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter) {
                return new Codec1.Builder<>(
                    new Field<>(name, codec, null, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder1<P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter) {
                return new Codec1.Builder<>(
                    new Field<>(name, codec, () -> def, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder1<P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter) {
                return new Codec1.Builder<>(
                    new Field<>(name, codec, def, getter)
                );
            }
        }
    }

    static class Codec1<P0, T> implements ObjectCodec<T> {
        private final ObjectCodecBuilder.Function1<P0, T> ctor;
        private final Field<T, P0> field0;

        Codec1(ObjectCodecBuilder.Function1<P0, T> ctor, Field<T, P0> field0) {
            this.ctor = ctor;
            this.field0 = field0;
        }

        @Override
        public void encodeObj(T obj, JsonNode json) {
            field0.apply(obj, json);
        }

        @Override
        public T decodeObj(JsonNode json) {
            return ctor.apply(
                field0.get(json)
            );
        }

        static class Builder<P0, T> implements ObjectCodecBuilder.Builder1<P0, T> {
            private final Field<T, P0> field0;

            Builder(Field<T, P0> field0) {
                this.field0 = field0;
            }

            @Override
            public JsonCodec<T> build(ObjectCodecBuilder.Function1<P0, T> ctor) {
                return new Codec1<>(ctor, field0);
            }

            @Override
            public <P> ObjectCodecBuilder.Builder2<P0, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter) {
                return new Codec2.Builder<>(
                    field0,
                    new Field<>(name, codec, null, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder2<P0, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter) {
                return new Codec2.Builder<>(
                    field0,
                    new Field<>(name, codec, () -> def, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder2<P0, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter) {
                return new Codec2.Builder<>(
                    field0,
                    new Field<>(name, codec, def, getter)
                );
            }
        }
    }

    static class Codec2<P0, P1, T> implements ObjectCodec<T> {
        private final ObjectCodecBuilder.Function2<P0, P1, T> ctor;
        private final Field<T, P0> field0;
        private final Field<T, P1> field1;

        Codec2(ObjectCodecBuilder.Function2<P0, P1, T> ctor, Field<T, P0> field0, Field<T, P1> field1) {
            this.ctor = ctor;
            this.field0 = field0;
            this.field1 = field1;
        }

        @Override
        public void encodeObj(T obj, JsonNode json) {
            field0.apply(obj, json);
            field1.apply(obj, json);
        }

        @Override
        public T decodeObj(JsonNode json) {
            return ctor.apply(
                field0.get(json),
                field1.get(json)
            );
        }

        static class Builder<P0, P1, T> implements ObjectCodecBuilder.Builder2<P0, P1, T> {
            private final Field<T, P0> field0;
            private final Field<T, P1> field1;

            Builder(Field<T, P0> field0, Field<T, P1> field1) {
                this.field0 = field0;
                this.field1 = field1;
            }

            @Override
            public JsonCodec<T> build(ObjectCodecBuilder.Function2<P0, P1, T> ctor) {
                return new Codec2<>(ctor, field0, field1);
            }

            @Override
            public <P> ObjectCodecBuilder.Builder3<P0, P1, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter) {
                return new Codec3.Builder<>(
                    field0, field1,
                    new Field<>(name, codec, null, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder3<P0, P1, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter) {
                return new Codec3.Builder<>(
                    field0, field1,
                    new Field<>(name, codec, () -> def, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder3<P0, P1, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter) {
                return new Codec3.Builder<>(
                    field0, field1,
                    new Field<>(name, codec, def, getter)
                );
            }
        }
    }

    static class Codec3<P0, P1, P2, T> implements ObjectCodec<T> {
        private final ObjectCodecBuilder.Function3<P0, P1, P2, T> ctor;
        private final Field<T, P0> field0;
        private final Field<T, P1> field1;
        private final Field<T, P2> field2;

        Codec3(ObjectCodecBuilder.Function3<P0, P1, P2, T> ctor, Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2) {
            this.ctor = ctor;
            this.field0 = field0;
            this.field1 = field1;
            this.field2 = field2;
        }

        @Override
        public void encodeObj(T obj, JsonNode json) {
            field0.apply(obj, json);
            field1.apply(obj, json);
            field2.apply(obj, json);
        }

        @Override
        public T decodeObj(JsonNode json) {
            return ctor.apply(
                field0.get(json),
                field1.get(json),
                field2.get(json)
            );
        }

        static class Builder<P0, P1, P2, T> implements ObjectCodecBuilder.Builder3<P0, P1, P2, T> {
            private final Field<T, P0> field0;
            private final Field<T, P1> field1;
            private final Field<T, P2> field2;

            Builder(Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2) {
                this.field0 = field0;
                this.field1 = field1;
                this.field2 = field2;
            }

            @Override
            public JsonCodec<T> build(ObjectCodecBuilder.Function3<P0, P1, P2, T> ctor) {
                return new Codec3<>(ctor, field0, field1, field2);
            }

            @Override
            public <P> ObjectCodecBuilder.Builder4<P0, P1, P2, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter) {
                return new Codec4.Builder<>(
                    field0, field1, field2,
                    new Field<>(name, codec, null, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder4<P0, P1, P2, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter) {
                return new Codec4.Builder<>(
                    field0, field1, field2,
                    new Field<>(name, codec, () -> def, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder4<P0, P1, P2, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter) {
                return new Codec4.Builder<>(
                    field0, field1, field2,
                    new Field<>(name, codec, def, getter)
                );
            }
        }
    }

    static class Codec4<P0, P1, P2, P3, T> implements ObjectCodec<T> {
        private final ObjectCodecBuilder.Function4<P0, P1, P2, P3, T> ctor;
        private final Field<T, P0> field0;
        private final Field<T, P1> field1;
        private final Field<T, P2> field2;
        private final Field<T, P3> field3;

        Codec4(ObjectCodecBuilder.Function4<P0, P1, P2, P3, T> ctor, Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3) {
            this.ctor = ctor;
            this.field0 = field0;
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
        }

        @Override
        public void encodeObj(T obj, JsonNode json) {
            field0.apply(obj, json);
            field1.apply(obj, json);
            field2.apply(obj, json);
            field3.apply(obj, json);
        }

        @Override
        public T decodeObj(JsonNode json) {
            return ctor.apply(
                field0.get(json),
                field1.get(json),
                field2.get(json),
                field3.get(json)
            );
        }

        static class Builder<P0, P1, P2, P3, T> implements ObjectCodecBuilder.Builder4<P0, P1, P2, P3, T> {
            private final Field<T, P0> field0;
            private final Field<T, P1> field1;
            private final Field<T, P2> field2;
            private final Field<T, P3> field3;

            Builder(Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3) {
                this.field0 = field0;
                this.field1 = field1;
                this.field2 = field2;
                this.field3 = field3;
            }

            @Override
            public JsonCodec<T> build(ObjectCodecBuilder.Function4<P0, P1, P2, P3, T> ctor) {
                return new Codec4<>(ctor, field0, field1, field2, field3);
            }

            @Override
            public <P> ObjectCodecBuilder.Builder5<P0, P1, P2, P3, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter) {
                return new Codec5.Builder<>(
                    field0, field1, field2, field3,
                    new Field<>(name, codec, null, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder5<P0, P1, P2, P3, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter) {
                return new Codec5.Builder<>(
                    field0, field1, field2, field3,
                    new Field<>(name, codec, () -> def, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder5<P0, P1, P2, P3, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter) {
                return new Codec5.Builder<>(
                    field0, field1, field2, field3,
                    new Field<>(name, codec, def, getter)
                );
            }
        }
    }

    static class Codec5<P0, P1, P2, P3, P4, T> implements ObjectCodec<T> {
        private final ObjectCodecBuilder.Function5<P0, P1, P2, P3, P4, T> ctor;
        private final Field<T, P0> field0;
        private final Field<T, P1> field1;
        private final Field<T, P2> field2;
        private final Field<T, P3> field3;
        private final Field<T, P4> field4;

        Codec5(ObjectCodecBuilder.Function5<P0, P1, P2, P3, P4, T> ctor, Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4) {
            this.ctor = ctor;
            this.field0 = field0;
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
            this.field4 = field4;
        }

        @Override
        public void encodeObj(T obj, JsonNode json) {
            field0.apply(obj, json);
            field1.apply(obj, json);
            field2.apply(obj, json);
            field3.apply(obj, json);
            field4.apply(obj, json);
        }

        @Override
        public T decodeObj(JsonNode json) {
            return ctor.apply(
                field0.get(json),
                field1.get(json),
                field2.get(json),
                field3.get(json),
                field4.get(json)
            );
        }

        static class Builder<P0, P1, P2, P3, P4, T> implements ObjectCodecBuilder.Builder5<P0, P1, P2, P3, P4, T> {
            private final Field<T, P0> field0;
            private final Field<T, P1> field1;
            private final Field<T, P2> field2;
            private final Field<T, P3> field3;
            private final Field<T, P4> field4;

            Builder(Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4) {
                this.field0 = field0;
                this.field1 = field1;
                this.field2 = field2;
                this.field3 = field3;
                this.field4 = field4;
            }

            @Override
            public JsonCodec<T> build(ObjectCodecBuilder.Function5<P0, P1, P2, P3, P4, T> ctor) {
                return new Codec5<>(ctor, field0, field1, field2, field3, field4);
            }

            @Override
            public <P> ObjectCodecBuilder.Builder6<P0, P1, P2, P3, P4, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter) {
                return new Codec6.Builder<>(
                    field0, field1, field2, field3, field4,
                    new Field<>(name, codec, null, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder6<P0, P1, P2, P3, P4, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter) {
                return new Codec6.Builder<>(
                    field0, field1, field2, field3, field4,
                    new Field<>(name, codec, () -> def, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder6<P0, P1, P2, P3, P4, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter) {
                return new Codec6.Builder<>(
                    field0, field1, field2, field3, field4,
                    new Field<>(name, codec, def, getter)
                );
            }
        }
    }

    static class Codec6<P0, P1, P2, P3, P4, P5, T> implements ObjectCodec<T> {
        private final ObjectCodecBuilder.Function6<P0, P1, P2, P3, P4, P5, T> ctor;
        private final Field<T, P0> field0;
        private final Field<T, P1> field1;
        private final Field<T, P2> field2;
        private final Field<T, P3> field3;
        private final Field<T, P4> field4;
        private final Field<T, P5> field5;

        Codec6(ObjectCodecBuilder.Function6<P0, P1, P2, P3, P4, P5, T> ctor, Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4, Field<T, P5> field5) {
            this.ctor = ctor;
            this.field0 = field0;
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
            this.field4 = field4;
            this.field5 = field5;
        }

        @Override
        public void encodeObj(T obj, JsonNode json) {
            field0.apply(obj, json);
            field1.apply(obj, json);
            field2.apply(obj, json);
            field3.apply(obj, json);
            field4.apply(obj, json);
            field5.apply(obj, json);
        }

        @Override
        public T decodeObj(JsonNode json) {
            return ctor.apply(
                field0.get(json),
                field1.get(json),
                field2.get(json),
                field3.get(json),
                field4.get(json),
                field5.get(json)
            );
        }

        static class Builder<P0, P1, P2, P3, P4, P5, T> implements ObjectCodecBuilder.Builder6<P0, P1, P2, P3, P4, P5, T> {
            private final Field<T, P0> field0;
            private final Field<T, P1> field1;
            private final Field<T, P2> field2;
            private final Field<T, P3> field3;
            private final Field<T, P4> field4;
            private final Field<T, P5> field5;

            Builder(Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4, Field<T, P5> field5) {
                this.field0 = field0;
                this.field1 = field1;
                this.field2 = field2;
                this.field3 = field3;
                this.field4 = field4;
                this.field5 = field5;
            }

            @Override
            public JsonCodec<T> build(ObjectCodecBuilder.Function6<P0, P1, P2, P3, P4, P5, T> ctor) {
                return new Codec6<>(ctor, field0, field1, field2, field3, field4, field5);
            }

            @Override
            public <P> ObjectCodecBuilder.Builder7<P0, P1, P2, P3, P4, P5, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter) {
                return new Codec7.Builder<>(
                    field0, field1, field2, field3, field4, field5,
                    new Field<>(name, codec, null, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder7<P0, P1, P2, P3, P4, P5, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter) {
                return new Codec7.Builder<>(
                    field0, field1, field2, field3, field4, field5,
                    new Field<>(name, codec, () -> def, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder7<P0, P1, P2, P3, P4, P5, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter) {
                return new Codec7.Builder<>(
                    field0, field1, field2, field3, field4, field5,
                    new Field<>(name, codec, def, getter)
                );
            }
        }
    }

    static class Codec7<P0, P1, P2, P3, P4, P5, P6, T> implements ObjectCodec<T> {
        private final ObjectCodecBuilder.Function7<P0, P1, P2, P3, P4, P5, P6, T> ctor;
        private final Field<T, P0> field0;
        private final Field<T, P1> field1;
        private final Field<T, P2> field2;
        private final Field<T, P3> field3;
        private final Field<T, P4> field4;
        private final Field<T, P5> field5;
        private final Field<T, P6> field6;

        Codec7(ObjectCodecBuilder.Function7<P0, P1, P2, P3, P4, P5, P6, T> ctor, Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4, Field<T, P5> field5, Field<T, P6> field6) {
            this.ctor = ctor;
            this.field0 = field0;
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
            this.field4 = field4;
            this.field5 = field5;
            this.field6 = field6;
        }

        @Override
        public void encodeObj(T obj, JsonNode json) {
            field0.apply(obj, json);
            field1.apply(obj, json);
            field2.apply(obj, json);
            field3.apply(obj, json);
            field4.apply(obj, json);
            field5.apply(obj, json);
            field6.apply(obj, json);
        }

        @Override
        public T decodeObj(JsonNode json) {
            return ctor.apply(
                field0.get(json),
                field1.get(json),
                field2.get(json),
                field3.get(json),
                field4.get(json),
                field5.get(json),
                field6.get(json)
            );
        }

        static class Builder<P0, P1, P2, P3, P4, P5, P6, T> implements ObjectCodecBuilder.Builder7<P0, P1, P2, P3, P4, P5, P6, T> {
            private final Field<T, P0> field0;
            private final Field<T, P1> field1;
            private final Field<T, P2> field2;
            private final Field<T, P3> field3;
            private final Field<T, P4> field4;
            private final Field<T, P5> field5;
            private final Field<T, P6> field6;

            Builder(Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4, Field<T, P5> field5, Field<T, P6> field6) {
                this.field0 = field0;
                this.field1 = field1;
                this.field2 = field2;
                this.field3 = field3;
                this.field4 = field4;
                this.field5 = field5;
                this.field6 = field6;
            }

            @Override
            public JsonCodec<T> build(ObjectCodecBuilder.Function7<P0, P1, P2, P3, P4, P5, P6, T> ctor) {
                return new Codec7<>(ctor, field0, field1, field2, field3, field4, field5, field6);
            }

            @Override
            public <P> ObjectCodecBuilder.Builder8<P0, P1, P2, P3, P4, P5, P6, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter) {
                return new Codec8.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6,
                    new Field<>(name, codec, null, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder8<P0, P1, P2, P3, P4, P5, P6, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter) {
                return new Codec8.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6,
                    new Field<>(name, codec, () -> def, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder8<P0, P1, P2, P3, P4, P5, P6, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter) {
                return new Codec8.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6,
                    new Field<>(name, codec, def, getter)
                );
            }
        }
    }

    static class Codec8<P0, P1, P2, P3, P4, P5, P6, P7, T> implements ObjectCodec<T> {
        private final ObjectCodecBuilder.Function8<P0, P1, P2, P3, P4, P5, P6, P7, T> ctor;
        private final Field<T, P0> field0;
        private final Field<T, P1> field1;
        private final Field<T, P2> field2;
        private final Field<T, P3> field3;
        private final Field<T, P4> field4;
        private final Field<T, P5> field5;
        private final Field<T, P6> field6;
        private final Field<T, P7> field7;

        Codec8(ObjectCodecBuilder.Function8<P0, P1, P2, P3, P4, P5, P6, P7, T> ctor, Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4, Field<T, P5> field5, Field<T, P6> field6, Field<T, P7> field7) {
            this.ctor = ctor;
            this.field0 = field0;
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
            this.field4 = field4;
            this.field5 = field5;
            this.field6 = field6;
            this.field7 = field7;
        }

        @Override
        public void encodeObj(T obj, JsonNode json) {
            field0.apply(obj, json);
            field1.apply(obj, json);
            field2.apply(obj, json);
            field3.apply(obj, json);
            field4.apply(obj, json);
            field5.apply(obj, json);
            field6.apply(obj, json);
            field7.apply(obj, json);
        }

        @Override
        public T decodeObj(JsonNode json) {
            return ctor.apply(
                field0.get(json),
                field1.get(json),
                field2.get(json),
                field3.get(json),
                field4.get(json),
                field5.get(json),
                field6.get(json),
                field7.get(json)
            );
        }

        static class Builder<P0, P1, P2, P3, P4, P5, P6, P7, T> implements ObjectCodecBuilder.Builder8<P0, P1, P2, P3, P4, P5, P6, P7, T> {
            private final Field<T, P0> field0;
            private final Field<T, P1> field1;
            private final Field<T, P2> field2;
            private final Field<T, P3> field3;
            private final Field<T, P4> field4;
            private final Field<T, P5> field5;
            private final Field<T, P6> field6;
            private final Field<T, P7> field7;

            Builder(Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4, Field<T, P5> field5, Field<T, P6> field6, Field<T, P7> field7) {
                this.field0 = field0;
                this.field1 = field1;
                this.field2 = field2;
                this.field3 = field3;
                this.field4 = field4;
                this.field5 = field5;
                this.field6 = field6;
                this.field7 = field7;
            }

            @Override
            public JsonCodec<T> build(ObjectCodecBuilder.Function8<P0, P1, P2, P3, P4, P5, P6, P7, T> ctor) {
                return new Codec8<>(ctor, field0, field1, field2, field3, field4, field5, field6, field7);
            }

            @Override
            public <P> ObjectCodecBuilder.Builder9<P0, P1, P2, P3, P4, P5, P6, P7, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter) {
                return new Codec9.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7,
                    new Field<>(name, codec, null, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder9<P0, P1, P2, P3, P4, P5, P6, P7, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter) {
                return new Codec9.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7,
                    new Field<>(name, codec, () -> def, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder9<P0, P1, P2, P3, P4, P5, P6, P7, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter) {
                return new Codec9.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7,
                    new Field<>(name, codec, def, getter)
                );
            }
        }
    }

    static class Codec9<P0, P1, P2, P3, P4, P5, P6, P7, P8, T> implements ObjectCodec<T> {
        private final ObjectCodecBuilder.Function9<P0, P1, P2, P3, P4, P5, P6, P7, P8, T> ctor;
        private final Field<T, P0> field0;
        private final Field<T, P1> field1;
        private final Field<T, P2> field2;
        private final Field<T, P3> field3;
        private final Field<T, P4> field4;
        private final Field<T, P5> field5;
        private final Field<T, P6> field6;
        private final Field<T, P7> field7;
        private final Field<T, P8> field8;

        Codec9(ObjectCodecBuilder.Function9<P0, P1, P2, P3, P4, P5, P6, P7, P8, T> ctor, Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4, Field<T, P5> field5, Field<T, P6> field6, Field<T, P7> field7, Field<T, P8> field8) {
            this.ctor = ctor;
            this.field0 = field0;
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
            this.field4 = field4;
            this.field5 = field5;
            this.field6 = field6;
            this.field7 = field7;
            this.field8 = field8;
        }

        @Override
        public void encodeObj(T obj, JsonNode json) {
            field0.apply(obj, json);
            field1.apply(obj, json);
            field2.apply(obj, json);
            field3.apply(obj, json);
            field4.apply(obj, json);
            field5.apply(obj, json);
            field6.apply(obj, json);
            field7.apply(obj, json);
            field8.apply(obj, json);
        }

        @Override
        public T decodeObj(JsonNode json) {
            return ctor.apply(
                field0.get(json),
                field1.get(json),
                field2.get(json),
                field3.get(json),
                field4.get(json),
                field5.get(json),
                field6.get(json),
                field7.get(json),
                field8.get(json)
            );
        }

        static class Builder<P0, P1, P2, P3, P4, P5, P6, P7, P8, T> implements ObjectCodecBuilder.Builder9<P0, P1, P2, P3, P4, P5, P6, P7, P8, T> {
            private final Field<T, P0> field0;
            private final Field<T, P1> field1;
            private final Field<T, P2> field2;
            private final Field<T, P3> field3;
            private final Field<T, P4> field4;
            private final Field<T, P5> field5;
            private final Field<T, P6> field6;
            private final Field<T, P7> field7;
            private final Field<T, P8> field8;

            Builder(Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4, Field<T, P5> field5, Field<T, P6> field6, Field<T, P7> field7, Field<T, P8> field8) {
                this.field0 = field0;
                this.field1 = field1;
                this.field2 = field2;
                this.field3 = field3;
                this.field4 = field4;
                this.field5 = field5;
                this.field6 = field6;
                this.field7 = field7;
                this.field8 = field8;
            }

            @Override
            public JsonCodec<T> build(ObjectCodecBuilder.Function9<P0, P1, P2, P3, P4, P5, P6, P7, P8, T> ctor) {
                return new Codec9<>(ctor, field0, field1, field2, field3, field4, field5, field6, field7, field8);
            }

            @Override
            public <P> ObjectCodecBuilder.Builder10<P0, P1, P2, P3, P4, P5, P6, P7, P8, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter) {
                return new Codec10.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7, field8,
                    new Field<>(name, codec, null, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder10<P0, P1, P2, P3, P4, P5, P6, P7, P8, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter) {
                return new Codec10.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7, field8,
                    new Field<>(name, codec, () -> def, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder10<P0, P1, P2, P3, P4, P5, P6, P7, P8, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter) {
                return new Codec10.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7, field8,
                    new Field<>(name, codec, def, getter)
                );
            }
        }
    }

    static class Codec10<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, T> implements ObjectCodec<T> {
        private final ObjectCodecBuilder.Function10<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, T> ctor;
        private final Field<T, P0> field0;
        private final Field<T, P1> field1;
        private final Field<T, P2> field2;
        private final Field<T, P3> field3;
        private final Field<T, P4> field4;
        private final Field<T, P5> field5;
        private final Field<T, P6> field6;
        private final Field<T, P7> field7;
        private final Field<T, P8> field8;
        private final Field<T, P9> field9;

        Codec10(ObjectCodecBuilder.Function10<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, T> ctor, Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4, Field<T, P5> field5, Field<T, P6> field6, Field<T, P7> field7, Field<T, P8> field8, Field<T, P9> field9) {
            this.ctor = ctor;
            this.field0 = field0;
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
            this.field4 = field4;
            this.field5 = field5;
            this.field6 = field6;
            this.field7 = field7;
            this.field8 = field8;
            this.field9 = field9;
        }

        @Override
        public void encodeObj(T obj, JsonNode json) {
            field0.apply(obj, json);
            field1.apply(obj, json);
            field2.apply(obj, json);
            field3.apply(obj, json);
            field4.apply(obj, json);
            field5.apply(obj, json);
            field6.apply(obj, json);
            field7.apply(obj, json);
            field8.apply(obj, json);
            field9.apply(obj, json);
        }

        @Override
        public T decodeObj(JsonNode json) {
            return ctor.apply(
                field0.get(json),
                field1.get(json),
                field2.get(json),
                field3.get(json),
                field4.get(json),
                field5.get(json),
                field6.get(json),
                field7.get(json),
                field8.get(json),
                field9.get(json)
            );
        }

        static class Builder<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, T> implements ObjectCodecBuilder.Builder10<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, T> {
            private final Field<T, P0> field0;
            private final Field<T, P1> field1;
            private final Field<T, P2> field2;
            private final Field<T, P3> field3;
            private final Field<T, P4> field4;
            private final Field<T, P5> field5;
            private final Field<T, P6> field6;
            private final Field<T, P7> field7;
            private final Field<T, P8> field8;
            private final Field<T, P9> field9;

            Builder(Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4, Field<T, P5> field5, Field<T, P6> field6, Field<T, P7> field7, Field<T, P8> field8, Field<T, P9> field9) {
                this.field0 = field0;
                this.field1 = field1;
                this.field2 = field2;
                this.field3 = field3;
                this.field4 = field4;
                this.field5 = field5;
                this.field6 = field6;
                this.field7 = field7;
                this.field8 = field8;
                this.field9 = field9;
            }

            @Override
            public JsonCodec<T> build(ObjectCodecBuilder.Function10<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, T> ctor) {
                return new Codec10<>(ctor, field0, field1, field2, field3, field4, field5, field6, field7, field8, field9);
            }

            @Override
            public <P> ObjectCodecBuilder.Builder11<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter) {
                return new Codec11.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7, field8, field9,
                    new Field<>(name, codec, null, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder11<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter) {
                return new Codec11.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7, field8, field9,
                    new Field<>(name, codec, () -> def, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder11<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter) {
                return new Codec11.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7, field8, field9,
                    new Field<>(name, codec, def, getter)
                );
            }
        }
    }

    static class Codec11<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, T> implements ObjectCodec<T> {
        private final ObjectCodecBuilder.Function11<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, T> ctor;
        private final Field<T, P0> field0;
        private final Field<T, P1> field1;
        private final Field<T, P2> field2;
        private final Field<T, P3> field3;
        private final Field<T, P4> field4;
        private final Field<T, P5> field5;
        private final Field<T, P6> field6;
        private final Field<T, P7> field7;
        private final Field<T, P8> field8;
        private final Field<T, P9> field9;
        private final Field<T, P10> field10;

        Codec11(ObjectCodecBuilder.Function11<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, T> ctor, Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4, Field<T, P5> field5, Field<T, P6> field6, Field<T, P7> field7, Field<T, P8> field8, Field<T, P9> field9, Field<T, P10> field10) {
            this.ctor = ctor;
            this.field0 = field0;
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
            this.field4 = field4;
            this.field5 = field5;
            this.field6 = field6;
            this.field7 = field7;
            this.field8 = field8;
            this.field9 = field9;
            this.field10 = field10;
        }

        @Override
        public void encodeObj(T obj, JsonNode json) {
            field0.apply(obj, json);
            field1.apply(obj, json);
            field2.apply(obj, json);
            field3.apply(obj, json);
            field4.apply(obj, json);
            field5.apply(obj, json);
            field6.apply(obj, json);
            field7.apply(obj, json);
            field8.apply(obj, json);
            field9.apply(obj, json);
            field10.apply(obj, json);
        }

        @Override
        public T decodeObj(JsonNode json) {
            return ctor.apply(
                field0.get(json),
                field1.get(json),
                field2.get(json),
                field3.get(json),
                field4.get(json),
                field5.get(json),
                field6.get(json),
                field7.get(json),
                field8.get(json),
                field9.get(json),
                field10.get(json)
            );
        }

        static class Builder<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, T> implements ObjectCodecBuilder.Builder11<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, T> {
            private final Field<T, P0> field0;
            private final Field<T, P1> field1;
            private final Field<T, P2> field2;
            private final Field<T, P3> field3;
            private final Field<T, P4> field4;
            private final Field<T, P5> field5;
            private final Field<T, P6> field6;
            private final Field<T, P7> field7;
            private final Field<T, P8> field8;
            private final Field<T, P9> field9;
            private final Field<T, P10> field10;

            Builder(Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4, Field<T, P5> field5, Field<T, P6> field6, Field<T, P7> field7, Field<T, P8> field8, Field<T, P9> field9, Field<T, P10> field10) {
                this.field0 = field0;
                this.field1 = field1;
                this.field2 = field2;
                this.field3 = field3;
                this.field4 = field4;
                this.field5 = field5;
                this.field6 = field6;
                this.field7 = field7;
                this.field8 = field8;
                this.field9 = field9;
                this.field10 = field10;
            }

            @Override
            public JsonCodec<T> build(ObjectCodecBuilder.Function11<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, T> ctor) {
                return new Codec11<>(ctor, field0, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10);
            }

            @Override
            public <P> ObjectCodecBuilder.Builder12<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter) {
                return new Codec12.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10,
                    new Field<>(name, codec, null, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder12<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter) {
                return new Codec12.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10,
                    new Field<>(name, codec, () -> def, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder12<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter) {
                return new Codec12.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10,
                    new Field<>(name, codec, def, getter)
                );
            }
        }
    }

    static class Codec12<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, T> implements ObjectCodec<T> {
        private final ObjectCodecBuilder.Function12<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, T> ctor;
        private final Field<T, P0> field0;
        private final Field<T, P1> field1;
        private final Field<T, P2> field2;
        private final Field<T, P3> field3;
        private final Field<T, P4> field4;
        private final Field<T, P5> field5;
        private final Field<T, P6> field6;
        private final Field<T, P7> field7;
        private final Field<T, P8> field8;
        private final Field<T, P9> field9;
        private final Field<T, P10> field10;
        private final Field<T, P11> field11;

        Codec12(ObjectCodecBuilder.Function12<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, T> ctor, Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4, Field<T, P5> field5, Field<T, P6> field6, Field<T, P7> field7, Field<T, P8> field8, Field<T, P9> field9, Field<T, P10> field10, Field<T, P11> field11) {
            this.ctor = ctor;
            this.field0 = field0;
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
            this.field4 = field4;
            this.field5 = field5;
            this.field6 = field6;
            this.field7 = field7;
            this.field8 = field8;
            this.field9 = field9;
            this.field10 = field10;
            this.field11 = field11;
        }

        @Override
        public void encodeObj(T obj, JsonNode json) {
            field0.apply(obj, json);
            field1.apply(obj, json);
            field2.apply(obj, json);
            field3.apply(obj, json);
            field4.apply(obj, json);
            field5.apply(obj, json);
            field6.apply(obj, json);
            field7.apply(obj, json);
            field8.apply(obj, json);
            field9.apply(obj, json);
            field10.apply(obj, json);
            field11.apply(obj, json);
        }

        @Override
        public T decodeObj(JsonNode json) {
            return ctor.apply(
                field0.get(json),
                field1.get(json),
                field2.get(json),
                field3.get(json),
                field4.get(json),
                field5.get(json),
                field6.get(json),
                field7.get(json),
                field8.get(json),
                field9.get(json),
                field10.get(json),
                field11.get(json)
            );
        }

        static class Builder<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, T> implements ObjectCodecBuilder.Builder12<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, T> {
            private final Field<T, P0> field0;
            private final Field<T, P1> field1;
            private final Field<T, P2> field2;
            private final Field<T, P3> field3;
            private final Field<T, P4> field4;
            private final Field<T, P5> field5;
            private final Field<T, P6> field6;
            private final Field<T, P7> field7;
            private final Field<T, P8> field8;
            private final Field<T, P9> field9;
            private final Field<T, P10> field10;
            private final Field<T, P11> field11;

            Builder(Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4, Field<T, P5> field5, Field<T, P6> field6, Field<T, P7> field7, Field<T, P8> field8, Field<T, P9> field9, Field<T, P10> field10, Field<T, P11> field11) {
                this.field0 = field0;
                this.field1 = field1;
                this.field2 = field2;
                this.field3 = field3;
                this.field4 = field4;
                this.field5 = field5;
                this.field6 = field6;
                this.field7 = field7;
                this.field8 = field8;
                this.field9 = field9;
                this.field10 = field10;
                this.field11 = field11;
            }

            @Override
            public JsonCodec<T> build(ObjectCodecBuilder.Function12<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, T> ctor) {
                return new Codec12<>(ctor, field0, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11);
            }

            @Override
            public <P> ObjectCodecBuilder.Builder13<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter) {
                return new Codec13.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
                    new Field<>(name, codec, null, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder13<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter) {
                return new Codec13.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
                    new Field<>(name, codec, () -> def, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder13<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter) {
                return new Codec13.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11,
                    new Field<>(name, codec, def, getter)
                );
            }
        }
    }

    static class Codec13<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, T> implements ObjectCodec<T> {
        private final ObjectCodecBuilder.Function13<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, T> ctor;
        private final Field<T, P0> field0;
        private final Field<T, P1> field1;
        private final Field<T, P2> field2;
        private final Field<T, P3> field3;
        private final Field<T, P4> field4;
        private final Field<T, P5> field5;
        private final Field<T, P6> field6;
        private final Field<T, P7> field7;
        private final Field<T, P8> field8;
        private final Field<T, P9> field9;
        private final Field<T, P10> field10;
        private final Field<T, P11> field11;
        private final Field<T, P12> field12;

        Codec13(ObjectCodecBuilder.Function13<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, T> ctor, Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4, Field<T, P5> field5, Field<T, P6> field6, Field<T, P7> field7, Field<T, P8> field8, Field<T, P9> field9, Field<T, P10> field10, Field<T, P11> field11, Field<T, P12> field12) {
            this.ctor = ctor;
            this.field0 = field0;
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
            this.field4 = field4;
            this.field5 = field5;
            this.field6 = field6;
            this.field7 = field7;
            this.field8 = field8;
            this.field9 = field9;
            this.field10 = field10;
            this.field11 = field11;
            this.field12 = field12;
        }

        @Override
        public void encodeObj(T obj, JsonNode json) {
            field0.apply(obj, json);
            field1.apply(obj, json);
            field2.apply(obj, json);
            field3.apply(obj, json);
            field4.apply(obj, json);
            field5.apply(obj, json);
            field6.apply(obj, json);
            field7.apply(obj, json);
            field8.apply(obj, json);
            field9.apply(obj, json);
            field10.apply(obj, json);
            field11.apply(obj, json);
            field12.apply(obj, json);
        }

        @Override
        public T decodeObj(JsonNode json) {
            return ctor.apply(
                field0.get(json),
                field1.get(json),
                field2.get(json),
                field3.get(json),
                field4.get(json),
                field5.get(json),
                field6.get(json),
                field7.get(json),
                field8.get(json),
                field9.get(json),
                field10.get(json),
                field11.get(json),
                field12.get(json)
            );
        }

        static class Builder<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, T> implements ObjectCodecBuilder.Builder13<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, T> {
            private final Field<T, P0> field0;
            private final Field<T, P1> field1;
            private final Field<T, P2> field2;
            private final Field<T, P3> field3;
            private final Field<T, P4> field4;
            private final Field<T, P5> field5;
            private final Field<T, P6> field6;
            private final Field<T, P7> field7;
            private final Field<T, P8> field8;
            private final Field<T, P9> field9;
            private final Field<T, P10> field10;
            private final Field<T, P11> field11;
            private final Field<T, P12> field12;

            Builder(Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4, Field<T, P5> field5, Field<T, P6> field6, Field<T, P7> field7, Field<T, P8> field8, Field<T, P9> field9, Field<T, P10> field10, Field<T, P11> field11, Field<T, P12> field12) {
                this.field0 = field0;
                this.field1 = field1;
                this.field2 = field2;
                this.field3 = field3;
                this.field4 = field4;
                this.field5 = field5;
                this.field6 = field6;
                this.field7 = field7;
                this.field8 = field8;
                this.field9 = field9;
                this.field10 = field10;
                this.field11 = field11;
                this.field12 = field12;
            }

            @Override
            public JsonCodec<T> build(ObjectCodecBuilder.Function13<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, T> ctor) {
                return new Codec13<>(ctor, field0, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12);
            }

            @Override
            public <P> ObjectCodecBuilder.Builder14<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter) {
                return new Codec14.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12,
                    new Field<>(name, codec, null, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder14<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter) {
                return new Codec14.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12,
                    new Field<>(name, codec, () -> def, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder14<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter) {
                return new Codec14.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12,
                    new Field<>(name, codec, def, getter)
                );
            }
        }
    }

    static class Codec14<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, T> implements ObjectCodec<T> {
        private final ObjectCodecBuilder.Function14<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, T> ctor;
        private final Field<T, P0> field0;
        private final Field<T, P1> field1;
        private final Field<T, P2> field2;
        private final Field<T, P3> field3;
        private final Field<T, P4> field4;
        private final Field<T, P5> field5;
        private final Field<T, P6> field6;
        private final Field<T, P7> field7;
        private final Field<T, P8> field8;
        private final Field<T, P9> field9;
        private final Field<T, P10> field10;
        private final Field<T, P11> field11;
        private final Field<T, P12> field12;
        private final Field<T, P13> field13;

        Codec14(ObjectCodecBuilder.Function14<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, T> ctor, Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4, Field<T, P5> field5, Field<T, P6> field6, Field<T, P7> field7, Field<T, P8> field8, Field<T, P9> field9, Field<T, P10> field10, Field<T, P11> field11, Field<T, P12> field12, Field<T, P13> field13) {
            this.ctor = ctor;
            this.field0 = field0;
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
            this.field4 = field4;
            this.field5 = field5;
            this.field6 = field6;
            this.field7 = field7;
            this.field8 = field8;
            this.field9 = field9;
            this.field10 = field10;
            this.field11 = field11;
            this.field12 = field12;
            this.field13 = field13;
        }

        @Override
        public void encodeObj(T obj, JsonNode json) {
            field0.apply(obj, json);
            field1.apply(obj, json);
            field2.apply(obj, json);
            field3.apply(obj, json);
            field4.apply(obj, json);
            field5.apply(obj, json);
            field6.apply(obj, json);
            field7.apply(obj, json);
            field8.apply(obj, json);
            field9.apply(obj, json);
            field10.apply(obj, json);
            field11.apply(obj, json);
            field12.apply(obj, json);
            field13.apply(obj, json);
        }

        @Override
        public T decodeObj(JsonNode json) {
            return ctor.apply(
                field0.get(json),
                field1.get(json),
                field2.get(json),
                field3.get(json),
                field4.get(json),
                field5.get(json),
                field6.get(json),
                field7.get(json),
                field8.get(json),
                field9.get(json),
                field10.get(json),
                field11.get(json),
                field12.get(json),
                field13.get(json)
            );
        }

        static class Builder<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, T> implements ObjectCodecBuilder.Builder14<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, T> {
            private final Field<T, P0> field0;
            private final Field<T, P1> field1;
            private final Field<T, P2> field2;
            private final Field<T, P3> field3;
            private final Field<T, P4> field4;
            private final Field<T, P5> field5;
            private final Field<T, P6> field6;
            private final Field<T, P7> field7;
            private final Field<T, P8> field8;
            private final Field<T, P9> field9;
            private final Field<T, P10> field10;
            private final Field<T, P11> field11;
            private final Field<T, P12> field12;
            private final Field<T, P13> field13;

            Builder(Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4, Field<T, P5> field5, Field<T, P6> field6, Field<T, P7> field7, Field<T, P8> field8, Field<T, P9> field9, Field<T, P10> field10, Field<T, P11> field11, Field<T, P12> field12, Field<T, P13> field13) {
                this.field0 = field0;
                this.field1 = field1;
                this.field2 = field2;
                this.field3 = field3;
                this.field4 = field4;
                this.field5 = field5;
                this.field6 = field6;
                this.field7 = field7;
                this.field8 = field8;
                this.field9 = field9;
                this.field10 = field10;
                this.field11 = field11;
                this.field12 = field12;
                this.field13 = field13;
            }

            @Override
            public JsonCodec<T> build(ObjectCodecBuilder.Function14<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, T> ctor) {
                return new Codec14<>(ctor, field0, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13);
            }

            @Override
            public <P> ObjectCodecBuilder.Builder15<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter) {
                return new Codec15.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13,
                    new Field<>(name, codec, null, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder15<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter) {
                return new Codec15.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13,
                    new Field<>(name, codec, () -> def, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder15<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter) {
                return new Codec15.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13,
                    new Field<>(name, codec, def, getter)
                );
            }
        }
    }

    static class Codec15<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, T> implements ObjectCodec<T> {
        private final ObjectCodecBuilder.Function15<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, T> ctor;
        private final Field<T, P0> field0;
        private final Field<T, P1> field1;
        private final Field<T, P2> field2;
        private final Field<T, P3> field3;
        private final Field<T, P4> field4;
        private final Field<T, P5> field5;
        private final Field<T, P6> field6;
        private final Field<T, P7> field7;
        private final Field<T, P8> field8;
        private final Field<T, P9> field9;
        private final Field<T, P10> field10;
        private final Field<T, P11> field11;
        private final Field<T, P12> field12;
        private final Field<T, P13> field13;
        private final Field<T, P14> field14;

        Codec15(ObjectCodecBuilder.Function15<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, T> ctor, Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4, Field<T, P5> field5, Field<T, P6> field6, Field<T, P7> field7, Field<T, P8> field8, Field<T, P9> field9, Field<T, P10> field10, Field<T, P11> field11, Field<T, P12> field12, Field<T, P13> field13, Field<T, P14> field14) {
            this.ctor = ctor;
            this.field0 = field0;
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
            this.field4 = field4;
            this.field5 = field5;
            this.field6 = field6;
            this.field7 = field7;
            this.field8 = field8;
            this.field9 = field9;
            this.field10 = field10;
            this.field11 = field11;
            this.field12 = field12;
            this.field13 = field13;
            this.field14 = field14;
        }

        @Override
        public void encodeObj(T obj, JsonNode json) {
            field0.apply(obj, json);
            field1.apply(obj, json);
            field2.apply(obj, json);
            field3.apply(obj, json);
            field4.apply(obj, json);
            field5.apply(obj, json);
            field6.apply(obj, json);
            field7.apply(obj, json);
            field8.apply(obj, json);
            field9.apply(obj, json);
            field10.apply(obj, json);
            field11.apply(obj, json);
            field12.apply(obj, json);
            field13.apply(obj, json);
            field14.apply(obj, json);
        }

        @Override
        public T decodeObj(JsonNode json) {
            return ctor.apply(
                field0.get(json),
                field1.get(json),
                field2.get(json),
                field3.get(json),
                field4.get(json),
                field5.get(json),
                field6.get(json),
                field7.get(json),
                field8.get(json),
                field9.get(json),
                field10.get(json),
                field11.get(json),
                field12.get(json),
                field13.get(json),
                field14.get(json)
            );
        }

        static class Builder<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, T> implements ObjectCodecBuilder.Builder15<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, T> {
            private final Field<T, P0> field0;
            private final Field<T, P1> field1;
            private final Field<T, P2> field2;
            private final Field<T, P3> field3;
            private final Field<T, P4> field4;
            private final Field<T, P5> field5;
            private final Field<T, P6> field6;
            private final Field<T, P7> field7;
            private final Field<T, P8> field8;
            private final Field<T, P9> field9;
            private final Field<T, P10> field10;
            private final Field<T, P11> field11;
            private final Field<T, P12> field12;
            private final Field<T, P13> field13;
            private final Field<T, P14> field14;

            Builder(Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4, Field<T, P5> field5, Field<T, P6> field6, Field<T, P7> field7, Field<T, P8> field8, Field<T, P9> field9, Field<T, P10> field10, Field<T, P11> field11, Field<T, P12> field12, Field<T, P13> field13, Field<T, P14> field14) {
                this.field0 = field0;
                this.field1 = field1;
                this.field2 = field2;
                this.field3 = field3;
                this.field4 = field4;
                this.field5 = field5;
                this.field6 = field6;
                this.field7 = field7;
                this.field8 = field8;
                this.field9 = field9;
                this.field10 = field10;
                this.field11 = field11;
                this.field12 = field12;
                this.field13 = field13;
                this.field14 = field14;
            }

            @Override
            public JsonCodec<T> build(ObjectCodecBuilder.Function15<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, T> ctor) {
                return new Codec15<>(ctor, field0, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14);
            }

            @Override
            public <P> ObjectCodecBuilder.Builder16<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter) {
                return new Codec16.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14,
                    new Field<>(name, codec, null, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder16<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter) {
                return new Codec16.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14,
                    new Field<>(name, codec, () -> def, getter)
                );
            }

            @Override
            public <P> ObjectCodecBuilder.Builder16<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter) {
                return new Codec16.Builder<>(
                    field0, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14,
                    new Field<>(name, codec, def, getter)
                );
            }
        }
    }

    static class Codec16<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, T> implements ObjectCodec<T> {
        private final ObjectCodecBuilder.Function16<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, T> ctor;
        private final Field<T, P0> field0;
        private final Field<T, P1> field1;
        private final Field<T, P2> field2;
        private final Field<T, P3> field3;
        private final Field<T, P4> field4;
        private final Field<T, P5> field5;
        private final Field<T, P6> field6;
        private final Field<T, P7> field7;
        private final Field<T, P8> field8;
        private final Field<T, P9> field9;
        private final Field<T, P10> field10;
        private final Field<T, P11> field11;
        private final Field<T, P12> field12;
        private final Field<T, P13> field13;
        private final Field<T, P14> field14;
        private final Field<T, P15> field15;

        Codec16(ObjectCodecBuilder.Function16<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, T> ctor, Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4, Field<T, P5> field5, Field<T, P6> field6, Field<T, P7> field7, Field<T, P8> field8, Field<T, P9> field9, Field<T, P10> field10, Field<T, P11> field11, Field<T, P12> field12, Field<T, P13> field13, Field<T, P14> field14, Field<T, P15> field15) {
            this.ctor = ctor;
            this.field0 = field0;
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
            this.field4 = field4;
            this.field5 = field5;
            this.field6 = field6;
            this.field7 = field7;
            this.field8 = field8;
            this.field9 = field9;
            this.field10 = field10;
            this.field11 = field11;
            this.field12 = field12;
            this.field13 = field13;
            this.field14 = field14;
            this.field15 = field15;
        }

        @Override
        public void encodeObj(T obj, JsonNode json) {
            field0.apply(obj, json);
            field1.apply(obj, json);
            field2.apply(obj, json);
            field3.apply(obj, json);
            field4.apply(obj, json);
            field5.apply(obj, json);
            field6.apply(obj, json);
            field7.apply(obj, json);
            field8.apply(obj, json);
            field9.apply(obj, json);
            field10.apply(obj, json);
            field11.apply(obj, json);
            field12.apply(obj, json);
            field13.apply(obj, json);
            field14.apply(obj, json);
            field15.apply(obj, json);
        }

        @Override
        public T decodeObj(JsonNode json) {
            return ctor.apply(
                field0.get(json),
                field1.get(json),
                field2.get(json),
                field3.get(json),
                field4.get(json),
                field5.get(json),
                field6.get(json),
                field7.get(json),
                field8.get(json),
                field9.get(json),
                field10.get(json),
                field11.get(json),
                field12.get(json),
                field13.get(json),
                field14.get(json),
                field15.get(json)
            );
        }

        static class Builder<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, T> implements ObjectCodecBuilder.Builder16<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, T> {
            private final Field<T, P0> field0;
            private final Field<T, P1> field1;
            private final Field<T, P2> field2;
            private final Field<T, P3> field3;
            private final Field<T, P4> field4;
            private final Field<T, P5> field5;
            private final Field<T, P6> field6;
            private final Field<T, P7> field7;
            private final Field<T, P8> field8;
            private final Field<T, P9> field9;
            private final Field<T, P10> field10;
            private final Field<T, P11> field11;
            private final Field<T, P12> field12;
            private final Field<T, P13> field13;
            private final Field<T, P14> field14;
            private final Field<T, P15> field15;

            Builder(Field<T, P0> field0, Field<T, P1> field1, Field<T, P2> field2, Field<T, P3> field3, Field<T, P4> field4, Field<T, P5> field5, Field<T, P6> field6, Field<T, P7> field7, Field<T, P8> field8, Field<T, P9> field9, Field<T, P10> field10, Field<T, P11> field11, Field<T, P12> field12, Field<T, P13> field13, Field<T, P14> field14, Field<T, P15> field15) {
                this.field0 = field0;
                this.field1 = field1;
                this.field2 = field2;
                this.field3 = field3;
                this.field4 = field4;
                this.field5 = field5;
                this.field6 = field6;
                this.field7 = field7;
                this.field8 = field8;
                this.field9 = field9;
                this.field10 = field10;
                this.field11 = field11;
                this.field12 = field12;
                this.field13 = field13;
                this.field14 = field14;
                this.field15 = field15;
            }

            @Override
            public JsonCodec<T> build(ObjectCodecBuilder.Function16<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, T> ctor) {
                return new Codec16<>(ctor, field0, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15);
            }

        }
    }

}
