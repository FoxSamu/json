package dev.runefox.json.codec;

import java.util.function.Function;
import java.util.function.Supplier;

public final class ObjectCodecBuilder {
    private ObjectCodecBuilder() {
    }

    public static <T> Builder0<T> of(Class<T> type) {
        return new ObjectCodec.Codec0.Builder<>();
    }

    public interface Function0<T> {
        T apply();
    }

    public interface Builder0<T> {
        JsonCodec<T> build(Function0<T> ctor);
        <P> Builder1<P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter);
        <P> Builder1<P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter);
        <P> Builder1<P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter);
    }

    public interface Function1<P0, T> {
        T apply(P0 p0);
    }

    public interface Builder1<P0, T> {
        JsonCodec<T> build(Function1<P0, T> ctor);
        <P> Builder2<P0, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter);
        <P> Builder2<P0, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter);
        <P> Builder2<P0, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter);
    }

    public interface Function2<P0, P1, T> {
        T apply(P0 p0, P1 p1);
    }

    public interface Builder2<P0, P1, T> {
        JsonCodec<T> build(Function2<P0, P1, T> ctor);
        <P> Builder3<P0, P1, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter);
        <P> Builder3<P0, P1, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter);
        <P> Builder3<P0, P1, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter);
    }

    public interface Function3<P0, P1, P2, T> {
        T apply(P0 p0, P1 p1, P2 p2);
    }

    public interface Builder3<P0, P1, P2, T> {
        JsonCodec<T> build(Function3<P0, P1, P2, T> ctor);
        <P> Builder4<P0, P1, P2, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter);
        <P> Builder4<P0, P1, P2, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter);
        <P> Builder4<P0, P1, P2, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter);
    }

    public interface Function4<P0, P1, P2, P3, T> {
        T apply(P0 p0, P1 p1, P2 p2, P3 p3);
    }

    public interface Builder4<P0, P1, P2, P3, T> {
        JsonCodec<T> build(Function4<P0, P1, P2, P3, T> ctor);
        <P> Builder5<P0, P1, P2, P3, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter);
        <P> Builder5<P0, P1, P2, P3, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter);
        <P> Builder5<P0, P1, P2, P3, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter);
    }

    public interface Function5<P0, P1, P2, P3, P4, T> {
        T apply(P0 p0, P1 p1, P2 p2, P3 p3, P4 p4);
    }

    public interface Builder5<P0, P1, P2, P3, P4, T> {
        JsonCodec<T> build(Function5<P0, P1, P2, P3, P4, T> ctor);
        <P> Builder6<P0, P1, P2, P3, P4, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter);
        <P> Builder6<P0, P1, P2, P3, P4, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter);
        <P> Builder6<P0, P1, P2, P3, P4, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter);
    }

    public interface Function6<P0, P1, P2, P3, P4, P5, T> {
        T apply(P0 p0, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);
    }

    public interface Builder6<P0, P1, P2, P3, P4, P5, T> {
        JsonCodec<T> build(Function6<P0, P1, P2, P3, P4, P5, T> ctor);
        <P> Builder7<P0, P1, P2, P3, P4, P5, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter);
        <P> Builder7<P0, P1, P2, P3, P4, P5, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter);
        <P> Builder7<P0, P1, P2, P3, P4, P5, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter);
    }

    public interface Function7<P0, P1, P2, P3, P4, P5, P6, T> {
        T apply(P0 p0, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);
    }

    public interface Builder7<P0, P1, P2, P3, P4, P5, P6, T> {
        JsonCodec<T> build(Function7<P0, P1, P2, P3, P4, P5, P6, T> ctor);
        <P> Builder8<P0, P1, P2, P3, P4, P5, P6, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter);
        <P> Builder8<P0, P1, P2, P3, P4, P5, P6, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter);
        <P> Builder8<P0, P1, P2, P3, P4, P5, P6, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter);
    }

    public interface Function8<P0, P1, P2, P3, P4, P5, P6, P7, T> {
        T apply(P0 p0, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7);
    }

    public interface Builder8<P0, P1, P2, P3, P4, P5, P6, P7, T> {
        JsonCodec<T> build(Function8<P0, P1, P2, P3, P4, P5, P6, P7, T> ctor);
        <P> Builder9<P0, P1, P2, P3, P4, P5, P6, P7, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter);
        <P> Builder9<P0, P1, P2, P3, P4, P5, P6, P7, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter);
        <P> Builder9<P0, P1, P2, P3, P4, P5, P6, P7, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter);
    }

    public interface Function9<P0, P1, P2, P3, P4, P5, P6, P7, P8, T> {
        T apply(P0 p0, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8);
    }

    public interface Builder9<P0, P1, P2, P3, P4, P5, P6, P7, P8, T> {
        JsonCodec<T> build(Function9<P0, P1, P2, P3, P4, P5, P6, P7, P8, T> ctor);
        <P> Builder10<P0, P1, P2, P3, P4, P5, P6, P7, P8, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter);
        <P> Builder10<P0, P1, P2, P3, P4, P5, P6, P7, P8, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter);
        <P> Builder10<P0, P1, P2, P3, P4, P5, P6, P7, P8, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter);
    }

    public interface Function10<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, T> {
        T apply(P0 p0, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);
    }

    public interface Builder10<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, T> {
        JsonCodec<T> build(Function10<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, T> ctor);
        <P> Builder11<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter);
        <P> Builder11<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter);
        <P> Builder11<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter);
    }

    public interface Function11<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, T> {
        T apply(P0 p0, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10);
    }

    public interface Builder11<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, T> {
        JsonCodec<T> build(Function11<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, T> ctor);
        <P> Builder12<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter);
        <P> Builder12<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter);
        <P> Builder12<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter);
    }

    public interface Function12<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, T> {
        T apply(P0 p0, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11);
    }

    public interface Builder12<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, T> {
        JsonCodec<T> build(Function12<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, T> ctor);
        <P> Builder13<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter);
        <P> Builder13<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter);
        <P> Builder13<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter);
    }

    public interface Function13<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, T> {
        T apply(P0 p0, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11, P12 p12);
    }

    public interface Builder13<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, T> {
        JsonCodec<T> build(Function13<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, T> ctor);
        <P> Builder14<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter);
        <P> Builder14<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter);
        <P> Builder14<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter);
    }

    public interface Function14<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, T> {
        T apply(P0 p0, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11, P12 p12, P13 p13);
    }

    public interface Builder14<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, T> {
        JsonCodec<T> build(Function14<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, T> ctor);
        <P> Builder15<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter);
        <P> Builder15<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter);
        <P> Builder15<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter);
    }

    public interface Function15<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, T> {
        T apply(P0 p0, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11, P12 p12, P13 p13, P14 p14);
    }

    public interface Builder15<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, T> {
        JsonCodec<T> build(Function15<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, T> ctor);
        <P> Builder16<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P, T> with(String name, JsonCodec<P> codec, Function<T, P> getter);
        <P> Builder16<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P, T> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter);
        <P> Builder16<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P, T> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter);
    }

    public interface Function16<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, T> {
        T apply(P0 p0, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10, P11 p11, P12 p12, P13 p13, P14 p14, P15 p15);
    }

    public interface Builder16<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, T> {
        JsonCodec<T> build(Function16<P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, T> ctor);
    }

}
