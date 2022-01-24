package net.shadew.json.codec;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.shadew.json.JsonException;
import net.shadew.json.JsonNode;
import net.shadew.json.JsonType;

/**
 * A generic definition of the JSON structure that encodes a specific Java object. This interface allows you to encode
 * and decode Java objects to and from a JSON tree. A codec should be immutable.
 * <p>
 * A codec does not only encode or decode objects, it also manages the validity of the data structures. For example, you
 * might want to validate if a numeric property is always a positive number.
 * <p>
 * While you might want to implement this interface yourself, the idea is that codecs are used as components of other
 * codecs, to decode and encode parts of an object. After all, the JSON structure of one object can be neatly contained
 * within the JSON structure of another object, without having to alter the inner structure in any way.
 * <p>
 * Important is to consider that most codecs do <strong>NOT</strong> accept null values. In case of null values you
 * probably just want to exclude the property from the JSON tree entirely. If a codec does support null values, it
 * should be obviously noted in the documentation. In any other case, you may expect {@link NullPointerException}s when
 * passing null values into the {@link #encode} method. Note that the {@link #decode} method never has to accept null
 * values; in case a codec encodes null elements, it probably expects a {@linkplain JsonNode#NULL JSON null} instead.
 *
 * @param <A> The type of the objects this codec encodes. The codec will not check if the object encoded is a subclass
 *            of this type, but in most cases it is recommended that you make a separate codec for subclasses.
 */
public interface JsonCodec<A> {
    /**
     * Encodes the specified object into a JSON structure. By convention, this structure must be decodable when passed
     * into the {@link #decode} method of this same codec.
     *
     * @param obj The object to be encoded
     * @return The encoded object, as a JSON tree
     *
     * @throws JsonException If the object cannot be encoded into a valid JSON structure (i.e. one that can be decoded
     *                       by this codec)
     */
    JsonNode encode(A obj);

    /**
     * Decodes the specified JSON tree back into an object. By convention, any JSON structure returned by the {@link
     * #encode} method of this same codec must be decodable.
     *
     * @param json The object to be decoded
     * @return The decoded object
     *
     * @throws JsonException If the JSON structure is not valid (i.e. it cannot be decoded into a valid object).
     */
    A decode(JsonNode json);

    /**
     * The codec that encodes and decodes a {@link JsonNode}. This codec essentially returns the input directly when
     * encoding or decoding, without performing any specific checks.
     * <p>
     * In most cases, you do not need this codec. But it might come in handy when you want to alter a JSON structure
     * when encoding or decoding, or require it to have a certain structure without creating an alternate object.
     */
    JsonCodec<JsonNode> JSON_NODE = of(Function.identity(), Function.identity());

    /**
     * The codec that encodes any byte value. This does not assert if the number in the JSON tree is out of the bounds
     * of the {@code byte} type. Null values are not accepted.
     */
    JsonCodec<Byte> BYTE = of(JsonNode::number, JsonNode::asByte);

    /**
     * The codec that encodes any short value. This does not assert if the number in the JSON tree is out of the bounds
     * of the {@code short} type. Null values are not accepted.
     */
    JsonCodec<Short> SHORT = of(JsonNode::number, JsonNode::asShort);

    /**
     * The codec that encodes any int value. This does not assert if the number in the JSON tree is out of the bounds of
     * the {@code int} type. Null values are not accepted.
     */
    JsonCodec<Integer> INT = of(JsonNode::number, JsonNode::asInt);

    /**
     * The codec that encodes any long value. This does not assert if the number in the JSON tree is out of the bounds
     * of the {@code long} type. Null values are not accepted.
     */
    JsonCodec<Long> LONG = of(JsonNode::number, JsonNode::asLong);

    /**
     * The codec that encodes any float value. Null values are not accepted.
     */
    JsonCodec<Float> FLOAT = of(JsonNode::number, JsonNode::asFloat);

    /**
     * The codec that encodes any double value. Null values are not accepted.
     */
    JsonCodec<Double> DOUBLE = of(JsonNode::number, JsonNode::asDouble);

    /**
     * The codec that encodes any {@link BigInteger} value. Null values are not accepted.
     */
    JsonCodec<BigInteger> BIG_INTEGER = of(JsonNode::number, JsonNode::asBigInteger);

    /**
     * The codec that encodes any {@link BigDecimal} value. Null values are not accepted.
     */
    JsonCodec<BigDecimal> BIG_DECIMAL = of(JsonNode::number, JsonNode::asBigDecimal);

    /**
     * The codec that encodes a boolean value. Null values are not accepted.
     */
    JsonCodec<Boolean> BOOLEAN = of(JsonNode::bool, JsonNode::asBoolean);

    /**
     * The codec that encodes any string value. This codec will convert any other JSON primitive into its string
     * representation when decoding (e.g. the numeric JSON value {@code 6} will be converted to the string {@code "6"}).
     * It will always encode as a string. Null values are not accepted.
     */
    JsonCodec<String> STRING = of(JsonNode::string, JsonNode::asString);

    /**
     * The codec that encodes any string value. Unlike {@link #STRING}, it will only decode JSON strings, so any
     * numeric, boolean or null value is considered invalid when decoding. Null values are not accepted.
     */
    JsonCodec<String> EXACT_STRING = of(JsonNode::string, JsonNode::asExactString);

    /**
     * The codec that encodes any character value, as a JSON string. It fails decoding when the input JSON is not a
     * string of exactly one character, or a numeric value representing the exact unicode value (0 to 65535). Null
     * values are not accepted.
     */
    JsonCodec<Character> CHAR = of(
        ch -> JsonNode.string(ch.toString()),
        json -> {
            json.require(JsonType.NUMBER, JsonType.STRING);
            if (json.isString()) {
                String str = json.asExactString();
                if (str.length() != 1)
                    throw new JsonCodecException("Character expected, string length must be 1");
                return str.charAt(0);
            }
            int i = json.asInt();
            if (i < 0 || i > Character.MAX_VALUE)
                throw new JsonCodecException("Character expected, unicode value must be between 0 and " + Character.MAX_VALUE);
            return (char) i;
        }
    );

    /**
     * The codec that encodes any unicode code point value, as a JSON string. It fails decoding when the input JSON is
     * not a string of exactly one code point, or a numeric value representing the exact unicode value (0 to 0x10FFFF).
     * Null values are not accepted.
     */
    JsonCodec<Integer> CODE_POINT = of(
        ch -> JsonNode.string(new String(new int[] {ch}, 0, 1)),
        json -> {
            json.require(JsonType.NUMBER, JsonType.STRING);
            if (json.isString()) {
                String str = json.asExactString();
                if (str.codePointCount(0, str.length()) != 1)
                    throw new JsonCodecException("Unicode code point expected, string length must be 1 code point");
                return str.codePointAt(0);
            }
            int i = json.asInt();
            if (i < 0 || i > Character.MAX_CODE_POINT)
                throw new JsonCodecException("Unicode code point expected, unicode value must be between 0 and " + Character.MAX_CODE_POINT);
            return i;
        }
    );

    /**
     * The codec that encodes a {@link UUID}, as a string. It fails decoding when the input JSON is not a string
     * representing a valid UUID. Null values are not accepted.
     */
    JsonCodec<UUID> UUID = of(
        uuid -> JsonNode.string(uuid.toString()),
        wrapExceptions(json -> java.util.UUID.fromString(json.asString()))
    );

    /**
     * Creates a codec for {@link JsonEncodable}s.
     *
     * @param instanceFactory The instance factory, usually a method reference or a simple lambda. This factory should
     *                        not have to do extensive decoding
     * @return The created codec
     */
    static <E extends JsonEncodable> JsonCodec<E> ofEncodable(Function<JsonNode, E> instanceFactory) {
        return new EncodableCodec<>(instanceFactory);
    }

    /**
     * Creates a codec for {@link JsonEncodable}s.
     *
     * @param instanceFactory The instance factory
     * @return The created codec
     */
    static <E extends JsonEncodable> JsonCodec<E> ofEncodable(Supplier<E> instanceFactory) {
        return new EncodableCodec<>(json -> instanceFactory.get());
    }

    /**
     * Creates a codec that tries all the given codecs, in order, to encode or decode an object. If one fails, it moves
     * on to the next, and it only fails encoding or decoding when all the codecs fail.
     *
     * @param options The different codecs to use
     * @return The combined codec
     */
    @SafeVarargs
    static <A> JsonCodec<A> alternatives(JsonCodec<A>... options) {
        return new CombinedCodec<>(List.of(options));
    }

    /**
     * Maps a codec to encode another object than the base codec does, by converting the object back to the type of the
     * base codec, and vice versa when decoding.
     *
     * @param codec The base codec
     * @param map   The function that creates the new object from the base object
     * @param unmap The function that converts the new object into the base object
     * @return The mapped codec
     */
    static <A, N> JsonCodec<A> map(JsonCodec<N> codec, Function<N, A> map, Function<A, N> unmap) {
        return new MappedCodec<>(codec, map, unmap);
    }

    /**
     * Creates a codec that uses the specific functions as implementations of {@link #encode} and {@link #decode}. Use
     * of this is discouraged, just as implementing {@link JsonCodec} yourself. However, it might be handy to use this
     * when you want to encode your object as a simple JSON type, because it allows you to use lambdas instead of an
     * anonymous class.
     *
     * @param encode The encoder
     * @param decode The decoder
     * @return The created codec
     */
    static <A> JsonCodec<A> of(Function<A, JsonNode> encode, Function<JsonNode, A> decode) {
        return new BasicCodec<>(encode, decode);
    }

    /**
     * Wraps a function and ensures all exceptions are thrown as {@link JsonException}s.
     *
     * @param fn The function to wrap.
     */
    static <A, B> Function<A, B> wrapExceptions(Function<A, B> fn) {
        return a -> {
            try {
                return fn.apply(a);
            } catch (JsonException exc) {
                throw exc;
            } catch (Throwable thr) {
                throw new JsonCodecException(thr.getMessage(), thr);
            }
        };
    }

    /**
     * Returns a codec that encodes a {@link List}, of which all elements are encoded using the given codec.
     *
     * @param elementCodec The codec for the elements of the list
     * @return The list codec
     */
    static <A> JsonCodec<List<A>> listOf(JsonCodec<A> elementCodec) {
        return new ListCodec<>(elementCodec);
    }

    /**
     * Returns a codec that encodes a {@link List}, of which all elements are encoded using the given codec.
     *
     * @param elementCodec The codec for the elements of the list
     * @param maxLen       The maximum amount of elements that this list may contain, inclusive
     * @return The list codec
     */
    static <A> JsonCodec<List<A>> listOf(JsonCodec<A> elementCodec, int maxLen) {
        return new ListCodec<>(elementCodec, maxLen);
    }

    /**
     * Returns a codec that encodes a {@link List}, of which all elements are encoded using the given codec.
     *
     * @param elementCodec The codec for the elements of the list
     * @param minLen       The minimum amount of elements that this list may contain, inclusive
     * @param maxLen       The maximum amount of elements that this list may contain, inclusive
     * @return The list codec
     */
    static <A> JsonCodec<List<A>> listOf(JsonCodec<A> elementCodec, int minLen, int maxLen) {
        return new ListCodec<>(elementCodec, minLen, maxLen);
    }

    /**
     * Returns a codec that encodes a {@link Set}, of which all elements are encoded using the given codec.
     *
     * @param elementCodec The codec for the elements of the set
     * @return The set codec
     */
    static <A> JsonCodec<Set<A>> setOf(JsonCodec<A> elementCodec) {
        return new SetCodec<>(elementCodec);
    }

    /**
     * Returns a codec that encodes a {@link Map}, of which all values are encoded using the given codec. The keys are
     * mapped to and from strings using the given functions. Not to be confused with {@link #map} which does something
     * else.
     * <p>
     * Note: any exception thrown by the mapping functions will be wrapped in a {@link JsonCodecException}. So if you
     * use {@link Integer} keys, you can safely use the method reference {@link Integer#parseInt Integer::parseInt},
     * without having to care about dealing with the {@link NumberFormatException} if a key is invalid.
     *
     * @param elementCodec The codec for the values of the map
     * @param keyToString  Function that converts a key to a string representation that can be converted back to a key
     * @param stringToKey  Function that converts a string representation back into a key
     * @return The map codec
     */
    static <A, K> JsonCodec<Map<K, A>> mapOf(JsonCodec<A> elementCodec, Function<K, String> keyToString, Function<String, K> stringToKey) {
        return new MapCodec<>(elementCodec, wrapExceptions(keyToString), wrapExceptions(stringToKey));
    }

    /**
     * Returns a codec that encodes a {@link Map}, of which all values are encoded using the given codec. The keys are
     * mapped to and from strings using the given functions. Not to be confused with {@link #map} which does something
     * else.
     *
     * @param elementCodec The codec for the values of the map
     * @return The map codec
     */
    static <A> JsonCodec<Map<String, A>> mapOf(JsonCodec<A> elementCodec) {
        return new MapCodec<>(elementCodec, Function.identity(), Function.identity());
    }

    /**
     * Returns a codec that encodes and decodes all bytes in the given range. Note that any JSON representation is first
     * converted, ignoring any potential precision loss.
     *
     * @param min The minimum value (inclusive)
     * @param max The maximum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<Byte> byteIn(byte min, byte max) {
        return new ComparableInCodec<>(BYTE, min, max);
    }

    /**
     * Returns a codec that encodes and decodes all bytes up to a given value. Note that any JSON representation is
     * first converted, ignoring any potential precision loss.
     *
     * @param max The maximum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<Byte> byteUnder(byte max) {
        return new ComparableUnderCodec<>(BYTE, max);
    }

    /**
     * Returns a codec that encodes and decodes all bytes from a given value. Note that any JSON representation is first
     * converted, ignoring any potential precision loss.
     *
     * @param min The minimum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<Byte> byteAbove(byte min) {
        return new ComparableAboveCodec<>(BYTE, min);
    }

    /**
     * Returns a codec that encodes and decodes all shorts in the given range. Note that any JSON representation is
     * first converted, ignoring any potential precision loss.
     *
     * @param min The minimum value (inclusive)
     * @param max The maximum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<Short> shortIn(short min, short max) {
        return new ComparableInCodec<>(SHORT, min, max);
    }

    /**
     * Returns a codec that encodes and decodes all shorts up to a given value. Note that any JSON representation is
     * first converted, ignoring any potential precision loss.
     *
     * @param max The maximum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<Short> shortUnder(short max) {
        return new ComparableUnderCodec<>(SHORT, max);
    }

    /**
     * Returns a codec that encodes and decodes all shorts from a given value. Note that any JSON representation is
     * first converted, ignoring any potential precision loss.
     *
     * @param min The minimum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<Short> shortAbove(short min) {
        return new ComparableAboveCodec<>(SHORT, min);
    }

    /**
     * Returns a codec that encodes and decodes all ints in the given range. Note that any JSON representation is first
     * converted, ignoring any potential precision loss.
     *
     * @param min The minimum value (inclusive)
     * @param max The maximum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<Integer> intIn(int min, int max) {
        return new ComparableInCodec<>(INT, min, max);
    }

    /**
     * Returns a codec that encodes and decodes all ints up to a given value. Note that any JSON representation is first
     * converted, ignoring any potential precision loss.
     *
     * @param max The maximum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<Integer> intUnder(int max) {
        return new ComparableUnderCodec<>(INT, max);
    }

    /**
     * Returns a codec that encodes and decodes all ints from a given value. Note that any JSON representation is first
     * converted, ignoring any potential precision loss.
     *
     * @param min The minimum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<Integer> intAbove(int min) {
        return new ComparableAboveCodec<>(INT, min);
    }

    /**
     * Returns a codec that encodes and decodes all longs in the given range. Note that any JSON representation is first
     * converted, ignoring any potential precision loss.
     *
     * @param min The minimum value (inclusive)
     * @param max The maximum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<Long> longIn(long min, long max) {
        return new ComparableInCodec<>(LONG, min, max);
    }

    /**
     * Returns a codec that encodes and decodes all longs up to a given value. Note that any JSON representation is
     * first converted, ignoring any potential precision loss.
     *
     * @param max The maximum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<Long> longUnder(long max) {
        return new ComparableUnderCodec<>(LONG, max);
    }

    /**
     * Returns a codec that encodes and decodes all longs from a given value. Note that any JSON representation is first
     * converted, ignoring any potential precision loss.
     *
     * @param min The minimum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<Long> longAbove(long min) {
        return new ComparableAboveCodec<>(LONG, min);
    }

    /**
     * Returns a codec that encodes and decodes all floats in the given range
     *
     * @param min The minimum value (inclusive)
     * @param max The maximum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<Float> floatIn(float min, float max) {
        return new ComparableInCodec<>(FLOAT, min, max);
    }

    /**
     * Returns a codec that encodes and decodes all floats up to a given value
     *
     * @param max The maximum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<Float> floatUnder(float max) {
        return new ComparableUnderCodec<>(FLOAT, max);
    }

    /**
     * Returns a codec that encodes and decodes all floats from a given value.
     *
     * @param min The minimum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<Float> floatAbove(float min) {
        return new ComparableAboveCodec<>(FLOAT, min);
    }

    /**
     * Returns a codec that encodes and decodes all doubles in the given range
     *
     * @param min The minimum value (inclusive)
     * @param max The maximum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<Double> doubleIn(double min, double max) {
        return new ComparableInCodec<>(DOUBLE, min, max);
    }

    /**
     * Returns a codec that encodes and decodes all doubles up to a given value
     *
     * @param max The maximum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<Double> doubleUnder(double max) {
        return new ComparableUnderCodec<>(DOUBLE, max);
    }

    /**
     * Returns a codec that encodes and decodes all doubles from a given value.
     *
     * @param min The minimum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<Double> doubleAbove(double min) {
        return new ComparableAboveCodec<>(DOUBLE, min);
    }

    /**
     * Returns a codec that encodes and decodes all {@link BigInteger}s in the given range
     *
     * @param min The minimum value (inclusive)
     * @param max The maximum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<BigInteger> bigIntegerIn(BigInteger min, BigInteger max) {
        return new ComparableInCodec<>(BIG_INTEGER, min, max);
    }

    /**
     * Returns a codec that encodes and decodes all {@link BigInteger}s up to a given value
     *
     * @param max The maximum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<BigInteger> bigIntegerUnder(BigInteger max) {
        return new ComparableUnderCodec<>(BIG_INTEGER, max);
    }

    /**
     * Returns a codec that encodes and decodes all {@link BigInteger}s from a given value.
     *
     * @param min The minimum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<BigInteger> bigIntegerAbove(BigInteger min) {
        return new ComparableAboveCodec<>(BIG_INTEGER, min);
    }

    /**
     * Returns a codec that encodes and decodes all {@link BigDecimal}s in the given range
     *
     * @param min The minimum value (inclusive)
     * @param max The maximum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<BigDecimal> bigDecimalIn(BigDecimal min, BigDecimal max) {
        return new ComparableInCodec<>(BIG_DECIMAL, min, max);
    }

    /**
     * Returns a codec that encodes and decodes all {@link BigDecimal}s up to a given value
     *
     * @param max The maximum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<BigDecimal> bigDecimalUnder(BigDecimal max) {
        return new ComparableUnderCodec<>(BIG_DECIMAL, max);
    }

    /**
     * Returns a codec that encodes and decodes all {@link BigDecimal}s from a given value.
     *
     * @param min The minimum value (inclusive)
     * @return The created codec
     */
    static JsonCodec<BigDecimal> bigDecimalAbove(BigDecimal min) {
        return new ComparableAboveCodec<>(BIG_DECIMAL, min);
    }

    /**
     * Returns a codec that encodes and decodes all {@link Comparable}s in the given range
     *
     * @param codec The base codec, that encodes any value of the same type
     * @param min   The minimum value (inclusive)
     * @param max   The maximum value (inclusive)
     * @return The created codec
     */
    static <A extends Comparable<A>> JsonCodec<A> in(JsonCodec<A> codec, A min, A max) {
        return new ComparableInCodec<>(codec, min, max);
    }

    /**
     * Returns a codec that encodes and decodes all {@link Comparable}s up to a given value
     *
     * @param codec The base codec, that encodes any value of the same type
     * @param max   The maximum value (inclusive)
     * @return The created codec
     */
    static <A extends Comparable<A>> JsonCodec<A> under(JsonCodec<A> codec, A max) {
        return new ComparableUnderCodec<>(codec, max);
    }

    /**
     * Returns a codec that encodes and decodes all {@link Comparable}s from a given value
     *
     * @param codec The base codec, that encodes any value of the same type
     * @param min   The minimum value (inclusive)
     * @return The created codec
     */
    static <A extends Comparable<A>> JsonCodec<A> above(JsonCodec<A> codec, A min) {
        return new ComparableAboveCodec<>(codec, min);
    }

    /**
     * Returns a codec that encodes and decodes an enum to and from a string.
     *
     * @param type  The class of the enum
     * @param namer A function that returns a string that identifies an enum value. Must return a unique string for
     *              every enum constant. This is called only before this method returns to set up a table of names
     * @return The enum codec
     */
    static <E extends Enum<E>> JsonCodec<E> ofEnum(Class<E> type, Function<E, String> namer) {
        return new EnumCodec<>(type, namer, e -> true);
    }

    /**
     * Returns a codec that encodes and decodes an enum to and from a string. All enum values between the given bounds
     * are accepted. A null bound stands for the first or the last enum value (depending on which bound), and bounds are
     * inclusive.
     *
     * @param type  The class of the enum
     * @param namer A function that returns a string that identifies an enum value. Must return a unique string for
     *              every enum constant. This is called only before this method returns to set up a table of names
     * @param from  The first acceptable enum value (or null for the first enum value defined)
     * @param to    The last acceptable enum value (or null for the last enum value defined)
     * @return The enum codec
     */
    static <E extends Enum<E>> JsonCodec<E> ofEnumIn(Class<E> type, Function<E, String> namer, E from, E to) {
        if (from == null && to == null)
            return new EnumCodec<>(type, namer, e -> true);
        if (from == null)
            return new EnumCodec<>(type, namer, e -> e.compareTo(to) <= 0);
        if (to == null)
            return new EnumCodec<>(type, namer, e -> e.compareTo(from) >= 0);
        return new EnumCodec<>(type, namer, e -> e.compareTo(from) >= 0 && e.compareTo(to) <= 0);
    }

    /**
     * Returns a codec that encodes and decodes an enum to and from a string, accepting only the given set of enum
     * values.
     *
     * @param type    The class of the enum
     * @param namer   A function that returns a string that identifies an enum value. Must return a unique string for
     *                every enum constant. This is called only before this method returns to set up a table of names
     * @param options The only enum values that are valid
     * @return The enum codec
     */
    @SafeVarargs
    static <E extends Enum<E>> JsonCodec<E> ofEnum(Class<E> type, Function<E, String> namer, E... options) {
        Set<E> set = new HashSet<>(Arrays.asList(options));
        return new EnumCodec<>(type, namer, set::contains);
    }

    /**
     * Returns a codec that encodes and decodes an enum to and from a string, accepting only the given set of enum
     * values.
     *
     * @param type    The class of the enum
     * @param namer   A function that returns a string that identifies an enum value. Must return a unique string for
     *                every enum constant. This is called only before this method returns to set up a table of names
     * @param options The only enum values that are valid
     * @return The enum codec
     */
    static <E extends Enum<E>> JsonCodec<E> ofEnum(Class<E> type, Function<E, String> namer, Collection<? extends E> options) {
        Set<E> set = new HashSet<>(options);
        return new EnumCodec<>(type, namer, set::contains);
    }

    /**
     * Returns a codec that encodes and decodes an enum to and from a string, accepting only the enum values that pass
     * the given check
     *
     * @param type  The class of the enum
     * @param namer A function that returns a string that identifies an enum value. Must return a unique string for
     *              every enum constant. This is called only before this method returns to set up a table of names
     * @param check A predicate that returns true only for the enum values that are valid. This is called only before
     *              this method returns to build a list of valid enum constants.
     * @return The enum codec
     */
    static <E extends Enum<E>> JsonCodec<E> ofEnum(Class<E> type, Function<E, String> namer, Predicate<E> check) {
        return new EnumCodec<>(type, namer, check);
    }

    /**
     * Returns a codec that performs an additional check on the value encoded or decoded by the given base codec.
     *
     * @param codec     The base codec
     * @param predicate The check, if it returns false the codec fails
     * @param error     The error function, only called when the given predicate returns false, to compute the error
     *                  message
     * @return The checked codec
     */
    static <A> JsonCodec<A> check(JsonCodec<A> codec, Predicate<A> predicate, Function<A, String> error) {
        return new CheckCodec<>(codec, predicate, error);
    }

    /**
     * Returns a codec that performs an additional check on the value encoded or decoded by the given base codec.
     *
     * @param codec     The base codec
     * @param predicate The check, if it returns false the codec fails
     * @param error     The error function, only called when the given predicate returns false, to compute the error
     *                  message
     * @return The checked codec
     */
    static <A> JsonCodec<A> check(JsonCodec<A> codec, Predicate<A> predicate, Supplier<String> error) {
        return new CheckCodec<>(codec, predicate, a -> error.get());
    }

    /**
     * Returns a codec that performs an additional check on the value encoded or decoded by the given base codec.
     *
     * @param codec     The base codec
     * @param predicate The check, if it returns false the codec fails
     * @param error     The error message
     * @return The checked codec
     */
    static <A> JsonCodec<A> check(JsonCodec<A> codec, Predicate<A> predicate, String error) {
        return new CheckCodec<>(codec, predicate, a -> error);
    }

    /**
     * Returns a codec that performs an additional check on the value encoded or decoded by the given base codec. It
     * will use a default error message {@link "Invalid value ...."} (with the erroneous value in place of the
     * periods).
     *
     * @param codec     The base codec
     * @param predicate The check, if it returns false the codec fails
     * @return The checked codec
     */
    static <A> JsonCodec<A> check(JsonCodec<A> codec, Predicate<A> predicate) {
        return new CheckCodec<>(codec, predicate, a -> "Invalid value " + a);
    }

    /**
     * Returns a codec that encodes and decodes a string that must match the given pattern.
     *
     * @param pattern The pattern that must be matched (according to {@link Matcher#matches})
     * @return The string codec
     */
    static JsonCodec<String> stringMatching(String pattern) {
        return stringMatching(Pattern.compile(pattern));
    }

    /**
     * Returns a codec that encodes and decodes a string that must match the given pattern.
     *
     * @param pattern The pattern that must be matched (according to {@link Matcher#matches})
     * @return The string codec
     */
    static JsonCodec<String> stringMatching(Pattern pattern) {
        return new CheckCodec<>(
            STRING,
            str -> pattern.matcher(str).matches(),
            str -> "'" + str + "' does not match '" + pattern.pattern() + "'"
        );
    }

    /**
     * Returns a codec that encodes and decodes a string with a limited length
     *
     * @param minLen The minimum length (inclusive)
     * @param maxLen The maximum length (inclusive)
     * @return The string codec
     */
    static JsonCodec<String> string(int minLen, int maxLen) {
        return new CheckCodec<>(STRING, str -> {
            int len = str.length();
            return len >= minLen && len <= maxLen;
        }, str -> "String '" + str + "' length is out of range [" + minLen + "," + maxLen + "]");
    }

    /**
     * Returns a codec that encodes and decodes a string with a limited length
     *
     * @param maxLen The maximum length (inclusive)
     * @return The string codec
     */
    static JsonCodec<String> string(int maxLen) {
        return new CheckCodec<>(STRING, str -> {
            int len = str.length();
            return len <= maxLen;
        }, str -> "String '" + str + "' length is above limit " + maxLen + "");
    }
}
