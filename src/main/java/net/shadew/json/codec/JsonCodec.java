package net.shadew.json.codec;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import net.shadew.json.JsonNode;

public interface JsonCodec<A> {
    JsonNode encode(A obj);
    A decode(JsonNode json);

    JsonCodec<JsonNode> JSON_NODE = of(Function.identity(), Function.identity());
    JsonCodec<Byte> BYTE = of(JsonNode::number, JsonNode::asByte);
    JsonCodec<Short> SHORT = of(JsonNode::number, JsonNode::asShort);
    JsonCodec<Integer> INT = of(JsonNode::number, JsonNode::asInt);
    JsonCodec<Long> LONG = of(JsonNode::number, JsonNode::asLong);
    JsonCodec<Float> FLOAT = of(JsonNode::number, JsonNode::asFloat);
    JsonCodec<Double> DOUBLE = of(JsonNode::number, JsonNode::asDouble);
    JsonCodec<BigInteger> BIG_INTEGER = of(JsonNode::number, JsonNode::asBigInteger);
    JsonCodec<BigDecimal> BIG_DECIMAL = of(JsonNode::number, JsonNode::asBigDecimal);
    JsonCodec<Boolean> BOOLEAN = of(JsonNode::bool, JsonNode::asBoolean);
    JsonCodec<String> STRING = of(JsonNode::string, JsonNode::asString);
    JsonCodec<String> EXACT_STRING = of(JsonNode::string, JsonNode::asExactString);
    JsonCodec<Character> CHAR = of(
        ch -> JsonNode.string(ch.toString()),
        json -> {
            String str = json.asExactString();
            if (str.length() != 1)
                throw new JsonCodecException("Expected string of length 1");
            return str.charAt(0);
        }
    );
    JsonCodec<Integer> CODE_POINT = of(
        ch -> JsonNode.string(new String(new int[] {ch}, 0, 1)),
        json -> {
            String str = json.asExactString();
            if (str.codePointCount(0, str.length()) != 1)
                throw new JsonCodecException("Expected string with 1 code point");
            return str.codePointAt(0);
        }
    );
    JsonCodec<UUID> UUID = of(
        uuid -> JsonNode.string(uuid.toString()),
        wrapExceptions(json -> java.util.UUID.fromString(json.asString()))
    );

    static <E extends JsonEncodable> JsonCodec<E> ofEncodable(Function<JsonNode, E> instanceFactory) {
        return new EncodableCodec<>(instanceFactory);
    }

    static <E extends JsonEncodable> JsonCodec<E> ofEncodable(Supplier<E> instanceFactory) {
        return new EncodableCodec<>(json -> instanceFactory.get());
    }

    @SafeVarargs
    static <A> JsonCodec<A> alternatives(JsonCodec<A>... options) {
        return new CombinedCodec<>(List.of(options));
    }

    static <A, N> JsonCodec<A> map(JsonCodec<N> codec, Function<N, A> map, Function<A, N> unmap) {
        return new MappedCodec<>(codec, map, unmap);
    }

    static <A> JsonCodec<A> of(Function<A, JsonNode> encode, Function<JsonNode, A> decode) {
        return new BasicCodec<>(encode, decode);
    }

    static <A, B> Function<A, B> wrapExceptions(Function<A, B> fn) {
        return a -> {
            try {
                return fn.apply(a);
            } catch (Throwable thr) {
                throw new JsonCodecException(thr.getMessage(), thr);
            }
        };
    }

    static <A> JsonCodec<List<A>> listOf(JsonCodec<A> elementCodec) {
        return new ListCodec<>(elementCodec);
    }

    static <A> JsonCodec<List<A>> listOf(JsonCodec<A> elementCodec, int maxLen) {
        return new ListCodec<>(elementCodec, maxLen);
    }

    static <A> JsonCodec<List<A>> listOf(JsonCodec<A> elementCodec, int minLen, int maxLen) {
        return new ListCodec<>(elementCodec, minLen, maxLen);
    }

    static <A> JsonCodec<Set<A>> setOf(JsonCodec<A> elementCodec) {
        return new SetCodec<>(elementCodec);
    }

    static <A, K> JsonCodec<Map<K, A>> mapOf(JsonCodec<A> elementCodec, Function<K, String> keyToString, Function<String, K> stringToKey) {
        return new MapCodec<>(elementCodec, wrapExceptions(keyToString), wrapExceptions(stringToKey));
    }

    static <A> JsonCodec<Map<String, A>> mapOf(JsonCodec<A> elementCodec) {
        return new MapCodec<>(elementCodec, Function.identity(), Function.identity());
    }

    static JsonCodec<Byte> byteIn(byte min, byte max) {
        return new ComparableInCodec<>(BYTE, min, max);
    }

    static JsonCodec<Byte> byteUnder(byte max) {
        return new ComparableUnderCodec<>(BYTE, max);
    }

    static JsonCodec<Byte> byteAbove(byte min) {
        return new ComparableAboveCodec<>(BYTE, min);
    }

    static JsonCodec<Short> shortIn(short min, short max) {
        return new ComparableInCodec<>(SHORT, min, max);
    }

    static JsonCodec<Short> shortUnder(short max) {
        return new ComparableUnderCodec<>(SHORT, max);
    }

    static JsonCodec<Short> shortAbove(short min) {
        return new ComparableAboveCodec<>(SHORT, min);
    }

    static JsonCodec<Integer> intIn(int min, int max) {
        return new ComparableInCodec<>(INT, min, max);
    }

    static JsonCodec<Integer> intUnder(int max) {
        return new ComparableUnderCodec<>(INT, max);
    }

    static JsonCodec<Integer> intAbove(int min) {
        return new ComparableAboveCodec<>(INT, min);
    }

    static JsonCodec<Long> longIn(long min, long max) {
        return new ComparableInCodec<>(LONG, min, max);
    }

    static JsonCodec<Long> longUnder(long max) {
        return new ComparableUnderCodec<>(LONG, max);
    }

    static JsonCodec<Long> longAbove(long min) {
        return new ComparableAboveCodec<>(LONG, min);
    }

    static JsonCodec<Float> floatIn(float min, float max) {
        return new ComparableInCodec<>(FLOAT, min, max);
    }

    static JsonCodec<Float> floatUnder(float max) {
        return new ComparableUnderCodec<>(FLOAT, max);
    }

    static JsonCodec<Float> floatAbove(float min) {
        return new ComparableAboveCodec<>(FLOAT, min);
    }

    static JsonCodec<Double> doubleIn(double min, double max) {
        return new ComparableInCodec<>(DOUBLE, min, max);
    }

    static JsonCodec<Double> doubleUnder(double max) {
        return new ComparableUnderCodec<>(DOUBLE, max);
    }

    static JsonCodec<Double> doubleAbove(double min) {
        return new ComparableAboveCodec<>(DOUBLE, min);
    }

    static JsonCodec<BigInteger> bigIntegerIn(BigInteger min, BigInteger max) {
        return new ComparableInCodec<>(BIG_INTEGER, min, max);
    }

    static JsonCodec<BigInteger> bigIntegerUnder(BigInteger max) {
        return new ComparableUnderCodec<>(BIG_INTEGER, max);
    }

    static JsonCodec<BigInteger> bigIntegerAbove(BigInteger min) {
        return new ComparableAboveCodec<>(BIG_INTEGER, min);
    }

    static JsonCodec<BigDecimal> bigDecimalIn(BigDecimal min, BigDecimal max) {
        return new ComparableInCodec<>(BIG_DECIMAL, min, max);
    }

    static JsonCodec<BigDecimal> bigDecimalUnder(BigDecimal max) {
        return new ComparableUnderCodec<>(BIG_DECIMAL, max);
    }

    static JsonCodec<BigDecimal> bigDecimalAbove(BigDecimal min) {
        return new ComparableAboveCodec<>(BIG_DECIMAL, min);
    }

    static <A extends Comparable<A>> JsonCodec<A> in(JsonCodec<A> codec, A min, A max) {
        return new ComparableInCodec<>(codec, min, max);
    }

    static <A extends Comparable<A>> JsonCodec<A> under(JsonCodec<A> codec, A max) {
        return new ComparableUnderCodec<>(codec, max);
    }

    static <A extends Comparable<A>> JsonCodec<A> above(JsonCodec<A> codec, A min) {
        return new ComparableAboveCodec<>(codec, min);
    }

    static <E extends Enum<E>> JsonCodec<E> ofEnum(Class<E> type, Function<E, String> namer) {
        return new EnumCodec<>(type, namer, e -> true);
    }

    static <E extends Enum<E>> JsonCodec<E> ofEnumIn(Class<E> type, Function<E, String> namer, E from, E to) {
        if (from == null && to == null)
            return new EnumCodec<>(type, namer, e -> true);
        if (from == null)
            return new EnumCodec<>(type, namer, e -> e.compareTo(to) <= 0);
        if (to == null)
            return new EnumCodec<>(type, namer, e -> e.compareTo(from) >= 0);
        return new EnumCodec<>(type, namer, e -> e.compareTo(from) >= 0 && e.compareTo(to) <= 0);
    }

    @SafeVarargs
    static <E extends Enum<E>> JsonCodec<E> ofEnum(Class<E> type, Function<E, String> namer, E... options) {
        Set<E> set = new HashSet<>(Arrays.asList(options));
        return new EnumCodec<>(type, namer, set::contains);
    }

    static <E extends Enum<E>> JsonCodec<E> ofEnum(Class<E> type, Function<E, String> namer, Collection<? extends E> options) {
        Set<E> set = new HashSet<>(options);
        return new EnumCodec<>(type, namer, set::contains);
    }

    static <E extends Enum<E>> JsonCodec<E> ofEnum(Class<E> type, Function<E, String> namer, Predicate<E> check) {
        return new EnumCodec<>(type, namer, check);
    }

    static <A> JsonCodec<A> check(JsonCodec<A> codec, Predicate<A> predicate, Function<A, String> error) {
        return new CheckCodec<>(codec, predicate, error);
    }

    static <A> JsonCodec<A> check(JsonCodec<A> codec, Predicate<A> predicate, Supplier<String> error) {
        return new CheckCodec<>(codec, predicate, a -> error.get());
    }

    static <A> JsonCodec<A> check(JsonCodec<A> codec, Predicate<A> predicate, String error) {
        return new CheckCodec<>(codec, predicate, a -> error);
    }

    static <A> JsonCodec<A> check(JsonCodec<A> codec, Predicate<A> predicate) {
        return new CheckCodec<>(codec, predicate, a -> "Invalid value " + a);
    }

    static JsonCodec<String> stringMatching(String pattern) {
        return stringMatching(Pattern.compile(pattern));
    }

    static JsonCodec<String> stringMatching(Pattern pattern) {
        return new CheckCodec<>(
            STRING,
            str -> pattern.matcher(str).matches(),
            str -> "'" + str + "' does not match '" + pattern.pattern() + "'"
        );
    }

    static JsonCodec<String> string(int minLen, int maxLen) {
        return new CheckCodec<>(STRING, str -> {
            int len = str.length();
            return len >= minLen && len <= maxLen;
        }, str -> "String '" + str + "' length is out of range [" + minLen + "," + maxLen + "]");
    }

    static JsonCodec<String> string(int maxLen) {
        return new CheckCodec<>(STRING, str -> {
            int len = str.length();
            return len <= maxLen;
        }, str -> "String '" + str + "' length is above limit " + maxLen + "");
    }
}
