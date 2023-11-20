package dev.runefox.json.codec;

import dev.runefox.json.JsonNode;
import dev.runefox.json.NodeException;
import dev.runefox.json.NodeType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * @throws NodeException If the object cannot be encoded into a valid JSON structure (i.e. one that can be decoded
     *                       by this codec)
     */
    JsonNode encode(A obj);

    /**
     * Decodes the specified JSON tree back into an object. By convention, any JSON structure returned by the
     * {@link #encode} method of this same codec must be decodable.
     *
     * @param json The object to be decoded
     * @return The decoded object
     *
     * @throws NodeException If the JSON structure is not valid (i.e. it cannot be decoded into a valid object).
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
    JsonCodec<String> STRING = of(JsonNode::string, JsonNode::show);

    /**
     * The codec that encodes any string value. Unlike {@link #STRING}, it will only decode JSON strings, so any
     * numeric, boolean or null value is considered invalid when decoding. Null values are not accepted.
     */
    JsonCodec<String> EXACT_STRING = of(JsonNode::string, JsonNode::asString);

    /**
     * The codec that encodes any character value, as a JSON string. It fails decoding when the input JSON is not a
     * string of exactly one character, or a numeric value representing the exact unicode value (0 to 65535). Null
     * values are not accepted.
     */
    JsonCodec<Character> CHAR = of(
        ch -> JsonNode.string(ch.toString()),
        json -> {
            json.require(NodeType.NUMBER, NodeType.STRING);
            if (json.isString()) {
                String str = json.asString();
                if (str.length() != 1)
                    throw new CodecException("Character expected, string length must be 1");
                return str.charAt(0);
            }
            int i = json.asInt();
            if (i < 0 || i > Character.MAX_VALUE)
                throw new CodecException("Character expected, unicode value must be between 0 and " + Character.MAX_VALUE);
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
            json.require(NodeType.NUMBER, NodeType.STRING);
            if (json.isString()) {
                String str = json.asString();
                if (str.codePointCount(0, str.length()) != 1)
                    throw new CodecException("Unicode code point expected, string length must be 1 code point");
                return str.codePointAt(0);
            }
            int i = json.asInt();
            if (i < 0 || i > Character.MAX_CODE_POINT)
                throw new CodecException("Unicode code point expected, unicode value must be between 0 and " + Character.MAX_CODE_POINT);
            return i;
        }
    );

    /**
     * The codec that encodes a {@link UUID}, as a string. It fails decoding when the input JSON is not a string
     * representing a valid UUID. Null values are not accepted.
     */
    JsonCodec<UUID> UUID = of(
        uuid -> JsonNode.string(uuid.toString()),
        wrapExceptions(json -> java.util.UUID.fromString(json.show()))
    );

    JsonCodec<LocalDate> LOCAL_DATE = of(JsonNode::localDate, JsonNode::asLocalDate)
                                          .alternatively(temporal(DateTimeFormatter.ISO_LOCAL_DATE, LocalDate::from));

    JsonCodec<LocalDateTime> LOCAL_DATE_TIME = of(JsonNode::localDateTime, JsonNode::asLocalDateTime)
                                                   .alternatively(temporal(DateTimeFormatter.ISO_LOCAL_DATE_TIME, LocalDateTime::from));

    JsonCodec<LocalTime> LOCAL_TIME = of(JsonNode::localTime, JsonNode::asLocalTime)
                                          .alternatively(temporal(DateTimeFormatter.ISO_LOCAL_TIME, LocalTime::from));

    JsonCodec<OffsetDateTime> OFFSET_DATE_TIME = of(JsonNode::offsetDateTime, JsonNode::asOffsetDateTime)
                                                     .alternatively(temporal(DateTimeFormatter.ISO_OFFSET_DATE_TIME, OffsetDateTime::from));

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
        return new AlternatingCodec<>(List.of(options));
    }

    default JsonCodec<A> alternatively(JsonCodec<A> option) {
        return alternatives(this, option);
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
     * Maps a codec to encode another object than the base codec does, by converting the object back to the type of the
     * base codec, and vice versa when decoding.
     *
     * @param map   The function that creates the new object from the base object
     * @param unmap The function that converts the new object into the base object
     * @return The mapped codec
     */
    default <N> JsonCodec<N> map(Function<A, N> map, Function<N, A> unmap) {
        return map(this, map, unmap);
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
     * Creates a codec that encodes using the given implementation, and fails decoding immediately. This can be useful
     * in alternating codecs.
     *
     * @param encode The encoder
     * @return The created codec
     */
    static <A> JsonCodec<A> encodeOnly(Function<A, JsonNode> encode) {
        return new BasicCodec<>(encode, node -> {
            throw new NoCodecImplementation();
        });
    }

    /**
     * Creates a codec that decodes using the given implementation, and fails encoding immediately. This can be useful
     * in alternating codecs.
     *
     * @param decode The decoder
     * @return The created codec
     */
    static <A> JsonCodec<A> decodeOnly(Function<JsonNode, A> decode) {
        return new BasicCodec<>(obj -> {
            throw new NoCodecImplementation();
        }, decode);
    }

    /**
     * Wraps a function and ensures all exceptions are thrown as {@link NodeException}s.
     *
     * @param fn The function to wrap.
     */
    static <A, B> Function<A, B> wrapExceptions(Function<A, B> fn) {
        return a -> {
            try {
                return fn.apply(a);
            } catch (NodeException exc) {
                throw exc;
            } catch (Throwable thr) {
                throw new CodecException(thr.getMessage(), thr);
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
     * Returns a codec that encodes a {@link List}, of which all elements are encoded using this codec.
     *
     * @return The list codec
     */
    default JsonCodec<List<A>> listOf() {
        return listOf(this);
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
     * Returns a codec that encodes a {@link List}, of which all elements are encoded using this codec.
     *
     * @param maxLen The maximum amount of elements that this list may contain, inclusive
     * @return The list codec
     */
    default JsonCodec<List<A>> listOf(int maxLen) {
        return listOf(this, maxLen);
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
     * Returns a codec that encodes a {@link List}, of which all elements are encoded using this codec.
     *
     * @param minLen The minimum amount of elements that this list may contain, inclusive
     * @param maxLen The maximum amount of elements that this list may contain, inclusive
     * @return The list codec
     */
    default JsonCodec<List<A>> listOf(int minLen, int maxLen) {
        return listOf(this, minLen, maxLen);
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
     * Returns a codec that encodes a {@link Set}, of which all elements are encoded using this codec.
     *
     * @return The set codec
     */
    default JsonCodec<Set<A>> setOf() {
        return setOf(this);
    }

    /**
     * Returns a codec that encodes a {@link Map}, of which all values are encoded using the given codec. The keys are
     * mapped to and from strings using the given functions. Not to be confused with {@link #map} which does something
     * else.
     * <p>
     * Note: any exception thrown by the mapping functions will be wrapped in a {@link CodecException}. So if you
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
     * Returns a codec that encodes a {@link Map}, of which all values are encoded using this codec. The keys are mapped
     * to and from strings using the given functions. Not to be confused with {@link #map} which does something else.
     * <p>
     * Note: any exception thrown by the mapping functions will be wrapped in a {@link CodecException}. So if you
     * use {@link Integer} keys, you can safely use the method reference {@link Integer#parseInt Integer::parseInt},
     * without having to care about dealing with the {@link NumberFormatException} if a key is invalid.
     *
     * @param keyToString Function that converts a key to a string representation that can be converted back to a key
     * @param stringToKey Function that converts a string representation back into a key
     * @return The map codec
     */
    default <K> JsonCodec<Map<K, A>> mapOf(Function<K, String> keyToString, Function<String, K> stringToKey) {
        return mapOf(this, keyToString, stringToKey);
    }

    /**
     * Returns a codec that encodes a {@link Map}, of which all values are encoded using the given codec.
     *
     * @param elementCodec The codec for the values of the map
     * @return The map codec
     */
    static <A> JsonCodec<Map<String, A>> mapOf(JsonCodec<A> elementCodec) {
        return new MapCodec<>(elementCodec, Function.identity(), Function.identity());
    }

    /**
     * Returns a codec that encodes a {@link Map}, of which all values are encoded using this codec.
     *
     * @return The map codec
     */
    default JsonCodec<Map<String, A>> mapOf() {
        return mapOf(this);
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
    static <A extends Comparable<? super A>> JsonCodec<A> in(JsonCodec<A> codec, A min, A max) {
        return new ComparableInCodec<>(codec, min, max);
    }

    /**
     * Returns a codec that encodes and decodes all {@link Comparable}s up to a given value
     *
     * @param codec The base codec, that encodes any value of the same type
     * @param max   The maximum value (inclusive)
     * @return The created codec
     */
    static <A extends Comparable<? super A>> JsonCodec<A> under(JsonCodec<A> codec, A max) {
        return new ComparableUnderCodec<>(codec, max);
    }

    /**
     * Returns a codec that encodes and decodes all {@link Comparable}s from a given value
     *
     * @param codec The base codec, that encodes any value of the same type
     * @param min   The minimum value (inclusive)
     * @return The created codec
     */
    static <A extends Comparable<? super A>> JsonCodec<A> above(JsonCodec<A> codec, A min) {
        return new ComparableAboveCodec<>(codec, min);
    }

    /**
     * Returns a codec that encodes and decodes all {@link Comparable}s in the given range
     *
     * @param codec The base codec, that encodes any value of the same type
     * @param min   The minimum value (inclusive)
     * @param max   The maximum value (inclusive)
     * @return The created codec
     */
    static <A> JsonCodec<A> in(JsonCodec<A> codec, A min, A max, Comparator<? super A> comp) {
        return new ComparatorInCodec<>(codec, min, max, comp);
    }

    /**
     * Returns a codec that encodes and decodes all {@link Comparable}s in the given range
     *
     * @param min The minimum value (inclusive)
     * @param max The maximum value (inclusive)
     * @return The created codec
     */
    default JsonCodec<A> in(A min, A max, Comparator<? super A> comp) {
        return in(this, min, max, comp);
    }

    /**
     * Returns a codec that encodes and decodes all {@link Comparable}s up to a given value
     *
     * @param codec The base codec, that encodes any value of the same type
     * @param max   The maximum value (inclusive)
     * @return The created codec
     */
    static <A> JsonCodec<A> under(JsonCodec<A> codec, A max, Comparator<? super A> comp) {
        return new ComparatorUnderCodec<>(codec, max, comp);
    }

    /**
     * Returns a codec that encodes and decodes all {@link Comparable}s up to a given value
     *
     * @param max The maximum value (inclusive)
     * @return The created codec
     */
    default JsonCodec<A> under(A max, Comparator<? super A> comp) {
        return under(this, max, comp);
    }

    /**
     * Returns a codec that encodes and decodes all {@link Comparable}s from a given value
     *
     * @param codec The base codec, that encodes any value of the same type
     * @param min   The minimum value (inclusive)
     * @return The created codec
     */
    static <A> JsonCodec<A> above(JsonCodec<A> codec, A min, Comparator<? super A> comp) {
        return new ComparatorAboveCodec<>(codec, min, comp);
    }

    /**
     * Returns a codec that encodes and decodes all {@link Comparable}s from a given value
     *
     * @param min The minimum value (inclusive)
     * @return The created codec
     */
    default JsonCodec<A> above(A min, Comparator<? super A> comp) {
        return above(this, min, comp);
    }

    /**
     * Returns a codec that encodes and decodes an enum to and from a string.
     *
     * @param type  The array of all enum values, as returned by {@code .values()}.
     * @param namer A function that returns a string that identifies an enum value. Must return a unique string for
     *              every enum constant. This is called only before this method returns to set up a table of names
     * @return The enum codec
     */
    static <E extends Enum<E>> JsonCodec<E> ofEnum(E[] type, Function<E, String> namer) {
        return new EnumCodec<>(type, namer, e -> true);
    }

    /**
     * Returns a codec that encodes and decodes an enum to and from a string. All enum values between the given bounds
     * are accepted. A null bound stands for the first or the last enum value (depending on which bound), and bounds are
     * inclusive.
     *
     * @param type  The array of all enum values, as returned by {@code .values()}.
     * @param namer A function that returns a string that identifies an enum value. Must return a unique string for
     *              every enum constant. This is called only before this method returns to set up a table of names
     * @param from  The first acceptable enum value (or null for the first enum value defined)
     * @param to    The last acceptable enum value (or null for the last enum value defined)
     * @return The enum codec
     */
    static <E extends Enum<E>> JsonCodec<E> ofEnumIn(E[] type, Function<E, String> namer, E from, E to) {
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
     * @param type    The array of all enum values, as returned by {@code .values()}.
     * @param namer   A function that returns a string that identifies an enum value. Must return a unique string for
     *                every enum constant. This is called only before this method returns to set up a table of names
     * @param options The only enum values that are valid
     * @return The enum codec
     */
    @SafeVarargs
    static <E extends Enum<E>> JsonCodec<E> ofEnum(E[] type, Function<E, String> namer, E... options) {
        Set<E> set = new HashSet<>(Arrays.asList(options));
        return new EnumCodec<>(type, namer, set::contains);
    }

    /**
     * Returns a codec that encodes and decodes an enum to and from a string, accepting only the given set of enum
     * values.
     *
     * @param type    The array of all enum values, as returned by {@code .values()}.
     * @param namer   A function that returns a string that identifies an enum value. Must return a unique string for
     *                every enum constant. This is called only before this method returns to set up a table of names
     * @param options The only enum values that are valid
     * @return The enum codec
     */
    static <E extends Enum<E>> JsonCodec<E> ofEnum(E[] type, Function<E, String> namer, Collection<? extends E> options) {
        Set<E> set = new HashSet<>(options);
        return new EnumCodec<>(type, namer, set::contains);
    }

    /**
     * Returns a codec that encodes and decodes an enum to and from a string, accepting only the enum values that pass
     * the given check
     *
     * @param type  The array of all enum values, as returned by {@code .values()}.
     * @param namer A function that returns a string that identifies an enum value. Must return a unique string for
     *              every enum constant. This is called only before this method returns to set up a table of names
     * @param check A predicate that returns true only for the enum values that are valid. This is called only before
     *              this method returns to build a list of valid enum constants.
     * @return The enum codec
     */
    static <E extends Enum<E>> JsonCodec<E> ofEnum(E[] type, Function<E, String> namer, Predicate<E> check) {
        return new EnumCodec<>(type, namer, check);
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
     * Returns a codec that performs an additional check on the value encoded or decoded by this codec.
     *
     * @param predicate The check, if it returns false the codec fails
     * @param error     The error function, only called when the given predicate returns false, to compute the error
     *                  message
     * @return The checked codec
     */
    default JsonCodec<A> check(Predicate<A> predicate, Function<A, String> error) {
        return check(this, predicate, error);
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
     * Returns a codec that performs an additional check on the value encoded or decoded by this codec.
     *
     * @param predicate The check, if it returns false the codec fails
     * @param error     The error function, only called when the given predicate returns false, to compute the error
     *                  message
     * @return The checked codec
     */
    default JsonCodec<A> check(Predicate<A> predicate, Supplier<String> error) {
        return check(this, predicate, error);
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
     * Returns a codec that performs an additional check on the value encoded or decoded by this codec.
     *
     * @param predicate The check, if it returns false the codec fails
     * @param error     The error message
     * @return The checked codec
     */
    default JsonCodec<A> check(Predicate<A> predicate, String error) {
        return check(this, predicate, error);
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
     * Returns a codec that performs an additional check on the value encoded or decoded by this codec. It will use a
     * default error message {@link "Invalid value ...."} (with the erroneous value in place of the periods).
     *
     * @param predicate The check, if it returns false the codec fails
     * @return The checked codec
     */
    default JsonCodec<A> check(Predicate<A> predicate) {
        return check(this, predicate);
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



    // TODO Document these

    static <T extends TemporalAccessor> JsonCodec<T> temporal(DateTimeFormatter formatter, TemporalQuery<T> query) {
        return new TemporalCodec<>(formatter, query);
    }


    static <T extends TemporalAccessor & Comparable<T>> JsonCodec<T> temporalIn(DateTimeFormatter formatter, T min, T max, TemporalQuery<T> query) {
        return in(new TemporalCodec<>(formatter, query), min, max);
    }

    static <T extends TemporalAccessor & Comparable<T>> JsonCodec<T> temporalAbove(DateTimeFormatter formatter, T min, TemporalQuery<T> query) {
        return above(new TemporalCodec<>(formatter, query), min);
    }

    static <T extends TemporalAccessor & Comparable<T>> JsonCodec<T> temporalUnder(DateTimeFormatter formatter, T max, TemporalQuery<T> query) {
        return under(new TemporalCodec<>(formatter, query), max);
    }


    static JsonCodec<Instant> instant(DateTimeFormatter formatter) {
        return temporal(formatter, Instant::from);
    }

    static JsonCodec<Instant> instantIn(DateTimeFormatter formatter, Instant min, Instant max) {
        return in(instant(formatter), min, max);
    }

    static JsonCodec<Instant> instantAbove(DateTimeFormatter formatter, Instant min) {
        return above(instant(formatter), min);
    }

    static JsonCodec<Instant> instantUnder(DateTimeFormatter formatter, Instant max) {
        return under(instant(formatter), max);
    }


    static JsonCodec<LocalDate> localDate(DateTimeFormatter formatter) {
        return temporal(formatter, LocalDate::from);
    }

    static JsonCodec<LocalDate> localDateIn(DateTimeFormatter formatter, LocalDate min, LocalDate max) {
        return in(localDate(formatter), min, max);
    }

    static JsonCodec<LocalDate> localDateAbove(DateTimeFormatter formatter, LocalDate min) {
        return above(localDate(formatter), min);
    }

    static JsonCodec<LocalDate> localDateUnder(DateTimeFormatter formatter, LocalDate max) {
        return under(localDate(formatter), max);
    }

    static JsonCodec<LocalDate> localDateIn(LocalDate min, LocalDate max) {
        return in(LOCAL_DATE, min, max);
    }

    static JsonCodec<LocalDate> localDateAbove(LocalDate min) {
        return above(LOCAL_DATE, min);
    }

    static JsonCodec<LocalDate> localDateUnder(LocalDate max) {
        return under(LOCAL_DATE, max);
    }


    static JsonCodec<LocalDateTime> localDateTime(DateTimeFormatter formatter) {
        return temporal(formatter, LocalDateTime::from);
    }

    static JsonCodec<LocalDateTime> localDateTimeIn(DateTimeFormatter formatter, LocalDateTime min, LocalDateTime max) {
        return in(localDateTime(formatter), min, max);
    }

    static JsonCodec<LocalDateTime> localDateTimeAbove(DateTimeFormatter formatter, LocalDateTime min) {
        return above(localDateTime(formatter), min);
    }

    static JsonCodec<LocalDateTime> localDateTimeUnder(DateTimeFormatter formatter, LocalDateTime max) {
        return under(localDateTime(formatter), max);
    }

    static JsonCodec<LocalDateTime> localDateTimeIn(LocalDateTime min, LocalDateTime max) {
        return in(LOCAL_DATE_TIME, min, max);
    }

    static JsonCodec<LocalDateTime> localDateTimeAbove(LocalDateTime min) {
        return above(LOCAL_DATE_TIME, min);
    }

    static JsonCodec<LocalDateTime> localDateTimeUnder(LocalDateTime max) {
        return under(LOCAL_DATE_TIME, max);
    }


    static JsonCodec<LocalTime> localTime(DateTimeFormatter formatter) {
        return temporal(formatter, LocalTime::from);
    }

    static JsonCodec<LocalTime> localTimeIn(DateTimeFormatter formatter, LocalTime min, LocalTime max) {
        return in(localTime(formatter), min, max);
    }

    static JsonCodec<LocalTime> localTimeAbove(DateTimeFormatter formatter, LocalTime min) {
        return above(localTime(formatter), min);
    }

    static JsonCodec<LocalTime> localTimeUnder(DateTimeFormatter formatter, LocalTime max) {
        return under(localTime(formatter), max);
    }

    static JsonCodec<LocalTime> localTimeIn(LocalTime min, LocalTime max) {
        return in(LOCAL_TIME, min, max);
    }

    static JsonCodec<LocalTime> localTimeAbove(LocalTime min) {
        return above(LOCAL_TIME, min);
    }

    static JsonCodec<LocalTime> localTimeUnder(LocalTime max) {
        return under(LOCAL_TIME, max);
    }


    static JsonCodec<OffsetDateTime> offsetDateTime(DateTimeFormatter formatter) {
        return temporal(formatter, OffsetDateTime::from);
    }

    static JsonCodec<OffsetDateTime> offsetDateTimeIn(DateTimeFormatter formatter, OffsetDateTime min, OffsetDateTime max) {
        return in(offsetDateTime(formatter), min, max);
    }

    static JsonCodec<OffsetDateTime> offsetDateTimeAbove(DateTimeFormatter formatter, OffsetDateTime min) {
        return above(offsetDateTime(formatter), min);
    }

    static JsonCodec<OffsetDateTime> offsetDateTimeUnder(DateTimeFormatter formatter, OffsetDateTime max) {
        return under(offsetDateTime(formatter), max);
    }

    static JsonCodec<OffsetDateTime> offsetDateTimeIn(OffsetDateTime min, OffsetDateTime max) {
        return in(OFFSET_DATE_TIME, min, max);
    }

    static JsonCodec<OffsetDateTime> offsetDateTimeAbove(OffsetDateTime min) {
        return above(OFFSET_DATE_TIME, min);
    }

    static JsonCodec<OffsetDateTime> offsetDateTimeUnder(OffsetDateTime max) {
        return under(OFFSET_DATE_TIME, max);
    }


    static JsonCodec<OffsetTime> offsetTime(DateTimeFormatter formatter) {
        return temporal(formatter, OffsetTime::from);
    }

    static JsonCodec<OffsetTime> offsetTimeIn(DateTimeFormatter formatter, OffsetTime min, OffsetTime max) {
        return in(offsetTime(formatter), min, max);
    }

    static JsonCodec<OffsetTime> offsetTimeAbove(DateTimeFormatter formatter, OffsetTime min) {
        return above(offsetTime(formatter), min);
    }

    static JsonCodec<OffsetTime> offsetTimeUnder(DateTimeFormatter formatter, OffsetTime max) {
        return under(offsetTime(formatter), max);
    }


    static JsonCodec<Year> year(DateTimeFormatter formatter) {
        return temporal(formatter, Year::from);
    }

    static JsonCodec<Year> yearIn(DateTimeFormatter formatter, Year min, Year max) {
        return in(year(formatter), min, max);
    }

    static JsonCodec<Year> yearAbove(DateTimeFormatter formatter, Year min) {
        return above(year(formatter), min);
    }

    static JsonCodec<Year> yearUnder(DateTimeFormatter formatter, Year max) {
        return under(year(formatter), max);
    }


    static JsonCodec<Month> month(DateTimeFormatter formatter) {
        return temporal(formatter, Month::from);
    }

    static JsonCodec<Month> monthIn(DateTimeFormatter formatter, Month min, Month max) {
        return in(month(formatter), min, max);
    }

    static JsonCodec<Month> monthAbove(DateTimeFormatter formatter, Month min) {
        return above(month(formatter), min);
    }

    static JsonCodec<Month> monthUnder(DateTimeFormatter formatter, Month max) {
        return under(month(formatter), max);
    }

    static JsonCodec<YearMonth> yearMonth(DateTimeFormatter formatter) {
        return temporal(formatter, YearMonth::from);
    }

    static JsonCodec<YearMonth> yearMonthIn(DateTimeFormatter formatter, YearMonth min, YearMonth max) {
        return in(yearMonth(formatter), min, max);
    }

    static JsonCodec<YearMonth> yearMonthAbove(DateTimeFormatter formatter, YearMonth min) {
        return above(yearMonth(formatter), min);
    }

    static JsonCodec<YearMonth> yearMonthUnder(DateTimeFormatter formatter, YearMonth max) {
        return under(yearMonth(formatter), max);
    }

    static JsonCodec<MonthDay> monthDay(DateTimeFormatter formatter) {
        return temporal(formatter, MonthDay::from);
    }

    static JsonCodec<MonthDay> monthDayIn(DateTimeFormatter formatter, MonthDay min, MonthDay max) {
        return in(monthDay(formatter), min, max);
    }

    static JsonCodec<MonthDay> monthDayAbove(DateTimeFormatter formatter, MonthDay min) {
        return above(monthDay(formatter), min);
    }

    static JsonCodec<MonthDay> monthDayUnder(DateTimeFormatter formatter, MonthDay max) {
        return under(monthDay(formatter), max);
    }


    static JsonCodec<ZonedDateTime> zonedDateTime(DateTimeFormatter formatter) {
        return temporal(formatter, ZonedDateTime::from);
    }

    static JsonCodec<ZonedDateTime> zonedDateTimeIn(DateTimeFormatter formatter, ZonedDateTime min, ZonedDateTime max) {
        return in(zonedDateTime(formatter), min, max);
    }

    static JsonCodec<ZonedDateTime> zonedDateTimeAbove(DateTimeFormatter formatter, ZonedDateTime min) {
        return above(zonedDateTime(formatter), min);
    }

    static JsonCodec<ZonedDateTime> zonedDateTimeUnder(DateTimeFormatter formatter, ZonedDateTime max) {
        return under(zonedDateTime(formatter), max);
    }
}
