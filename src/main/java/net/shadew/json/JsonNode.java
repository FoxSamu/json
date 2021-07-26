package net.shadew.json;

import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * A general interface representing a JSON tree. This interface covers any type a JSON node can be: a string, number,
 * boolean, null, array or object. The implementation is provided internally and this interface must not be manually
 * implemented.
 * <p>
 * A primitive node (null, a number, a string or a boolean) is immutable and any mutating of this interface will throw
 * an exception for them. Copying primitives is useless.
 * <ul>
 * <li>{@link #NULL} is the singleton null node</li>
 * <li>{@link #TRUE} is the singleton true boolean node</li>
 * <li>{@link #FALSE} is the singleton false boolean node</li>
 * <li>{@link #ZERO} is the singleton zero number node</li>
 * <li>{@link #EMPTY_STRING} is the singleton empty string node</li>
 * <li>{@link #bool} returns a boolean node</li>
 * <li>{@link #number} returns a number node</li>
 * <li>{@link #string} returns a string node</li>
 * </ul>
 * <p>
 * An array node is mutable and can be modified using array modification methods. Using object modification methods will
 * cause an exception, and so does trying to obtain the primitive value of an array. Some methods will work on both
 * arrays and objects.
 * <ul>
 * <li>{@link #array()} creates a new, empty array</li>
 * <li>{@link #array(JsonNode...)} creates an array with elements</li>
 * <li>{@link #array(Iterable)} creates an array with elements</li>
 * <li>{@link #stringArray(String...)} creates an array with strings</li>
 * <li>{@link #stringArray(Iterable)} creates an array with strings</li>
 * <li>{@link #numberArray(Number...)} creates an array with numbers</li>
 * <li>{@link #numberArray(Iterable)} creates an array with numbers</li>
 * <li>{@link #boolArray(Boolean...)} creates an array with booleans</li>
 * <li>{@link #boolArray(Iterable)} creates an array with booleans</li>
 * </ul>
 * <p>
 * An object node is mutable and can be modified using object modification methods. Using array modification methods will
 * cause an exception, and so does trying to obtain the primitive value of an object. Some methods will work on both
 * arrays and objects.
 * <ul>
 * <li>{@link #object()} creates a new, empty object</li>
 * <li>{@link #object(Map)} creates an object with elements</li>
 * <li>{@link #stringObject(Map)} creates an object with strings</li>
 * <li>{@link #numberObject(Map)} creates an object with numbers</li>
 * <li>{@link #boolObject(Map)} creates an object with booleans</li>
 * </ul>
 * <p>
 * Java's default methods, {@link #equals}, {@link #hashCode} and {@link #toString} work on {@link JsonNode} instances.
 * Converting a node to a string will generate a quick and compact JSON representation of the node. This is useful for
 * debugging purposes, but it's not recommended when writing to a file or sending over a network.
 */
public interface JsonNode extends Iterable<JsonNode> {
    /**
     * The only {@link JsonNode} representing the JSON value {@code null}.
     */
    JsonNode NULL = new NullNode();

    /**
     * The only {@link JsonNode} representing the JSON boolean value {@code true}.
     */
    JsonNode TRUE = new BooleanNode(true);

    /**
     * The only {@link JsonNode} representing the JSON boolean value {@code false}.
     */
    JsonNode FALSE = new BooleanNode(false);

    /**
     * The only {@link JsonNode} representing the JSON string value {@code ""}.
     */
    JsonNode EMPTY_STRING = new StringNode("");

    /**
     * The only {@link JsonNode} representing the JSON number value {@code 0}.
     */
    JsonNode ZERO = new NumberNode(0);

    /**
     * Returns {@code node}, or the {@link #NULL} instance if {@code node} is null. This ensures the returned value is
     * never null. This also checks whether the given {@link JsonNode} is a correct implementation, and not an external
     * implementation.
     *
     * @param node A nullable JSON tree.
     * @return The given JSON tree, or {@link #NULL}.
     *
     * @throws IllegalArgumentException When the given {@link JsonNode} implementation is not the correct, internal
     *                                  implementation.
     */
    static JsonNode orNull(JsonNode node) {
        if (node == null)
            return NULL;

        if (!(node instanceof AbstractJsonNode))
            throw new IllegalArgumentException("JsonNode implementation is not a builtin implementation. This breaks");

        return node;
    }

    /**
     * Returns a boolean-type node with the given boolean value. This returns {@link #TRUE} if the given value is true,
     * and {@link #FALSE} if the given value is false. This returns {@link #NULL} if the given value is null.
     *
     * @param value The boolean value
     * @return The JSON boolean node
     */
    static JsonNode bool(Boolean value) {
        if (value == null) return NULL;
        return value ? TRUE : FALSE;
    }

    /**
     * Returns a string-type node with the given string value, {@link #EMPTY_STRING} if the given string is empty, or
     * {@link #NULL} if the given string is null.
     *
     * @param value The string value
     * @return The JSON string node
     */
    static JsonNode string(String value) {
        if (value == null) return NULL;
        return value.isEmpty() ? EMPTY_STRING : new StringNode(value);
    }

    /**
     * Returns a number-type node with the given number value, {@link #ZERO} if the given number is zero, or {@link
     * #NULL} if the given number is null. Even though any valid {@link Number} subclass is valid, it is recommended to
     * use only Java's builtin number types (primitives, {@link BigInteger}, {@link BigDecimal}, etc.). Other number
     * types may cause unexpected or unpredictable behaviour.
     *
     * @param value The number value
     * @return The JSON number node
     */
    static JsonNode number(Number value) {
        if (value == null) return NULL;
        return value.intValue() == 0 ? ZERO : new NumberNode(value);
    }

    /**
     * Returns a new mutable, empty array node.
     *
     * @return The JSON array node
     */
    static JsonNode array() {
        return new ArrayNode();
    }

    /**
     * Returns a new mutable array node, initially filled with the given elements. Any null element is automatically
     * converted to {@link #NULL}.
     *
     * @param elems The elements in the array
     * @return The JSON array node
     */
    static JsonNode array(JsonNode... elems) {
        return new ArrayNode(elems);
    }

    /**
     * Returns a new mutable array node, initially filled with the given elements. Any null element is automatically
     * converted to {@link #NULL}.
     *
     * @param elems The elements in the array
     * @return The JSON array node
     */
    static JsonNode array(Iterable<? extends JsonNode> elems) {
        return new ArrayNode(elems);
    }

    /**
     * Returns a new mutable array node, initially filled with the given strings. Any null element is automatically
     * converted to {@link #NULL}.
     *
     * @param elems The elements in the array
     * @return The JSON array node
     */
    static JsonNode stringArray(String... elems) {
        ArrayNode node = new ArrayNode();
        for (String elem : elems)
            node.add(elem);
        return node;
    }

    /**
     * Returns a new mutable array node, initially filled with the given numbers. Any null element is automatically
     * converted to {@link #NULL}.
     *
     * @param elems The elements in the array
     * @return The JSON array node
     */
    static JsonNode numberArray(Number... elems) {
        ArrayNode node = new ArrayNode();
        for (Number elem : elems)
            node.add(elem);
        return node;
    }

    /**
     * Returns a new mutable array node, initially filled with the given booleans. Any null element is automatically
     * converted to {@link #NULL}.
     *
     * @param elems The elements in the array
     * @return The JSON array node
     */
    static JsonNode boolArray(Boolean... elems) {
        ArrayNode node = new ArrayNode();
        for (Boolean elem : elems)
            node.add(elem);
        return node;
    }

    /**
     * Returns a new mutable array node, initially filled with the given strings. Any null element is automatically
     * converted to {@link #NULL}.
     *
     * @param elems The elements in the array
     * @return The JSON array node
     */
    static JsonNode stringArray(Iterable<? extends String> elems) {
        ArrayNode node = new ArrayNode();
        for (String elem : elems)
            node.add(elem);
        return node;
    }

    /**
     * Returns a new mutable array node, initially filled with the given number. Any null element is automatically
     * converted to {@link #NULL}.
     *
     * @param elems The elements in the array
     * @return The JSON array node
     */
    static JsonNode numberArray(Iterable<? extends Number> elems) {
        ArrayNode node = new ArrayNode();
        for (Number elem : elems)
            node.add(elem);
        return node;
    }

    /**
     * Returns a new mutable array node, initially filled with the given booleans. Any null element is automatically
     * converted to {@link #NULL}.
     *
     * @param elems The elements in the array
     * @return The JSON array node
     */
    static JsonNode boolArray(Iterable<Boolean> elems) {
        ArrayNode node = new ArrayNode();
        for (Boolean elem : elems)
            node.add(elem);
        return node;
    }

    /**
     * Returns a new mutable array node, initially filled with the given amount of null nodes.
     *
     * @param size The amount of elements in the array
     * @return The JSON array node
     */
    static JsonNode nullArray(int size) {
        ArrayNode node = new ArrayNode();
        while (size-- > 0)
            node.add(NULL);
        return node;
    }

    /**
     * Returns a new mutable, empty object node.
     *
     * @return The JSON object node
     */
    static JsonNode object() {
        return new ObjectNode();
    }

    /**
     * Returns a new mutable object node, initially filled with the elements from the given map. Elements are inserted
     * in the given map's iteration order (which is preserved). Any null element is automatically replaced with {@link
     * #NULL}.
     *
     * @param elems The elements to be initially in the object
     * @return The JSON object node
     */
    static JsonNode object(Map<? extends String, ? extends JsonNode> elems) {
        return new ObjectNode(elems);
    }

    /**
     * Returns a new mutable object node, initially filled with the strings from the given map. Elements are inserted in
     * the given map's iteration order (which is preserved). Any null element is automatically replaced with {@link
     * #NULL}.
     *
     * @param elems The strings to be initially in the object
     * @return The JSON object node
     */
    static JsonNode stringObject(Map<? extends String, ? extends String> elems) {
        ObjectNode node = new ObjectNode();
        elems.forEach(node::set);
        return node;
    }

    /**
     * Returns a new mutable object node, initially filled with the numbers from the given map. Elements are inserted in
     * the given map's iteration order (which is preserved). Any null element is automatically replaced with {@link
     * #NULL}.
     *
     * @param elems The numbers to be initially in the object
     * @return The JSON object node
     */
    static JsonNode numberObject(Map<? extends String, ? extends Number> elems) {
        ObjectNode node = new ObjectNode();
        elems.forEach(node::set);
        return node;
    }

    /**
     * Returns a new mutable object node, initially filled with the booleans from the given map. Elements are inserted
     * in the given map's iteration order (which is preserved). Any null element is automatically replaced with {@link
     * #NULL}.
     *
     * @param elems The booleans to be initially in the object
     * @return The JSON object node
     */
    static JsonNode boolObject(Map<? extends String, ? extends Boolean> elems) {
        ObjectNode node = new ObjectNode();
        elems.forEach(node::set);
        return node;
    }

    /**
     * Attempts to convert a Java-based structure (i.e. Maps, Lists, etc.) to a JSON tree.
     * <p>
     * <strong>Warning:</strong>
     * Even though this method allows to obtain a JSON tree quickly from pretty much any basic builtin type, this method
     * is not recommended for production use. This method does some implicit conversions to primitive types in order to
     * create a suitable JSON structure, which can happen in semi-unpredictable ways. One might prefer more control over
     * how instances are converted to JSON. Use with a light risk.
     * <p>
     * This method will convert types in the following order:
     * <ul>
     * <li><code>null</code> is converted to {@link #NULL}</li>
     * <li>Any {@link JsonNode} is returned by itself</li>
     * <li>Any {@link JsonSerializable} is converted via {@link JsonSerializable#toJsonTree()}, implement this interface to allow quick serialization</li>
     * <li>Any {@link Boolean} is converted to a boolean node</li>
     * <li>Any {@link Number} is converted to a number node</li>
     * <li>Any {@link String} is converted to a string node</li>
     * <li>Any {@link File}, {@link Path}, {@link URL}, {@link URI} or {@link JsonPath} is converted into a string via {@link Object#toString()}</li>
     * <li>Any {@link Enum} is converted into its name via {@link Enum#name()}</li>
     * <li>Any {@link Stream} is converted to an array node with the streamed elements</li>
     * <li>Any {@link Iterable} is converted to an array node with the iterated elements</li>
     * <li>Any {@link Iterator} is converted to an array node with the remaining elements</li>
     * <li>Any {@link Enumeration} is converted to an array node with the remaining elements</li>
     * <li>Any {@link Map} is converted to an object node with the contained elements, keys converted to string as by {@link #toString()}</li>
     * <li>Any {@link Dictionary} is converted to an object node with the contained elements, keys converted to string as by {@link #toString()}</li>
     * <li>Any array type (primitive or not) is converted to an array node with the contained elements</li>
     * <li>Any other object is converted to a string like {@link Object#toString()}</li>
     * </ul>
     * Conversions of objects in iterables/arrays/maps/etc. are recursively converted using this method as well.
     *
     * @param obj The object to convert.
     * @return The converted JSON tree
     */
    static JsonNode fromJavaObject(Object obj) {
        if (obj == null) {
            // Null
            return NULL;
        } else if (obj instanceof JsonNode) {
            // Any node is returned by itself
            return (JsonNode) obj;
        } else if (obj instanceof JsonSerializable) {
            // Serialize anything specifically serializable in the way it wants to
            return orNull(((JsonSerializable) obj).toJsonTree());
        } else if (obj instanceof Boolean) {
            // Boolean
            return bool((Boolean) obj);
        } else if (obj instanceof Number) {
            // Number
            return number((Number) obj);
        } else if (obj instanceof String) {
            // String
            return string((String) obj);
        } else if (obj instanceof File) {
            return string(obj.toString());
        } else if (obj instanceof Path) {
            return string(obj.toString());
        } else if (obj instanceof URL) {
            return string(obj.toString());
        } else if (obj instanceof URI) {
            return string(obj.toString());
        } else if (obj instanceof JsonPath) {
            return string(obj.toString());
        } else if (obj instanceof Enum) {
            return string(((Enum<?>) obj).name());
        } else if (obj instanceof Stream) {
            // Stream -> Array
            Stream<?> itr = (Stream<?>) obj;
            ArrayNode node = new ArrayNode();
            itr.map(JsonNode::fromJavaObject).forEach(node::add);
            return node;
        } else if (obj instanceof Iterable) {
            // Iterable -> Array
            Iterable<?> itr = (Iterable<?>) obj;
            ArrayNode node = new ArrayNode();
            for (Object el : itr)
                node.add(fromJavaObject(el));
            return node;
        } else if (obj instanceof Iterator) {
            // Iterator -> Array
            Iterator<?> itr = (Iterator<?>) obj;
            ArrayNode node = new ArrayNode();
            while (itr.hasNext())
                node.add(fromJavaObject(itr.next()));
            return node;
        } else if (obj instanceof Enumeration) {
            // Enumeration -> Array
            Enumeration<?> itr = (Enumeration<?>) obj;
            ArrayNode node = new ArrayNode();
            while (itr.hasMoreElements())
                node.add(fromJavaObject(itr.nextElement()));
            return node;
        } else if (obj instanceof Map) {
            // Map -> Object
            Map<?, ?> map = (Map<?, ?>) obj;
            ObjectNode node = new ObjectNode();
            map.forEach((k, v) -> node.set(k + "", fromJavaObject(v)));
            return node;
        } else if (obj instanceof Dictionary) {
            // Dictionary -> Object
            Dictionary<?, ?> map = (Dictionary<?, ?>) obj;
            ObjectNode node = new ObjectNode();
            Enumeration<?> keys = map.keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                node.set(key + "", fromJavaObject(map.get(key)));
            }
            return node;
        } else if (obj.getClass().isArray()) {
            // Array -> Array
            // Using reflection it's possible to cover all array types at once
            int len = Array.getLength(obj);
            ArrayNode node = new ArrayNode();
            for (int i = 0; i < len; i++) {
                node.add(fromJavaObject(Array.get(obj, i)));
            }
            return node;
        } else {
            // Other: convert to string
            return string(obj + "");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Checks
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the {@linkplain JsonType type} of this node.
     *
     * @return The type of this node
     */
    JsonType type();

    /**
     * Returns true when this node is a {@linkplain JsonType#NULL null} node.
     *
     * @return Whether this node is a null node
     */
    boolean isNull();

    /**
     * Returns true when this node is a {@linkplain JsonType#STRING string} node.
     *
     * @return Whether this node is a string node
     */
    boolean isString();

    /**
     * Returns true when this node is a {@linkplain JsonType#NUMBER number} node.
     *
     * @return Whether this node is a number node
     */
    boolean isNumber();

    /**
     * Returns true when this node is a {@linkplain JsonType#BOOLEAN boolean} node.
     *
     * @return Whether this node is a boolean node
     */
    boolean isBoolean();

    /**
     * Returns true when this node is a {@linkplain JsonType#OBJECT object} node.
     *
     * @return Whether this node is a object node
     */
    boolean isObject();

    /**
     * Returns true when this node is a {@linkplain JsonType#ARRAY array} node.
     *
     * @return Whether this node is a array node
     */
    boolean isArray();

    /**
     * Returns true when this node is of the specified type.
     *
     * @return Whether this node is a specific type
     */
    boolean is(JsonType type);

    /**
     * Returns true when this node is of one of the specified types.
     *
     * @return Whether this node is one of the specified types
     */
    boolean is(JsonType... types);

    /**
     * Throws an {@link IncorrectTypeException} when this node is not {@linkplain JsonType#NULL null}
     *
     * @return This instance for chaining
     *
     * @throws IncorrectTypeException When the this node is not null
     */
    JsonNode requireNull();

    /**
     * Throws an {@link IncorrectTypeException} when this node is not {@linkplain JsonType#STRING string}
     *
     * @return This instance for chaining
     *
     * @throws IncorrectTypeException When the this node is not string
     */
    JsonNode requireString();

    /**
     * Throws an {@link IncorrectTypeException} when this node is not {@linkplain JsonType#NUMBER number}
     *
     * @return This instance for chaining
     *
     * @throws IncorrectTypeException When the this node is not number
     */
    JsonNode requireNumber();

    /**
     * Throws an {@link IncorrectTypeException} when this node is not {@linkplain JsonType#BOOLEAN boolean}
     *
     * @return This instance for chaining
     *
     * @throws IncorrectTypeException When the this node is not boolean
     */
    JsonNode requireBoolean();

    /**
     * Throws an {@link IncorrectTypeException} when this node is not {@linkplain JsonType#OBJECT object}
     *
     * @return This instance for chaining
     *
     * @throws IncorrectTypeException When the this node is not object
     */
    JsonNode requireObject();

    /**
     * Throws an {@link IncorrectTypeException} when this node is not {@linkplain JsonType#ARRAY array}
     *
     * @return This instance for chaining
     *
     * @throws IncorrectTypeException When the this node is not array
     */
    JsonNode requireArray();

    /**
     * Throws an {@link IncorrectTypeException} when this node is not the given type
     *
     * @return This instance for chaining
     *
     * @throws IncorrectTypeException When the this node is not the given type
     */
    JsonNode require(JsonType type);

    /**
     * Throws an {@link IncorrectTypeException} when this node is not one of the given types
     *
     * @return This instance for chaining
     *
     * @throws IncorrectTypeException When the this node is not one of the given types
     */
    JsonNode require(JsonType... types);



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Conversions
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the string value of this node.
     *
     * @return The string value of this node
     *
     * @throws IncorrectTypeException When this node is not a string node
     */
    String asString();

    /**
     * Returns the byte value of this node.
     *
     * @return The byte value of this node
     *
     * @throws IncorrectTypeException When this node is not a number node
     */
    byte asByte();

    /**
     * Returns the short value of this node.
     *
     * @return The short value of this node
     *
     * @throws IncorrectTypeException When this node is not a number node
     */
    short asShort();

    /**
     * Returns the integer value of this node.
     *
     * @return The integer value of this node
     *
     * @throws IncorrectTypeException When this node is not a number node
     */
    int asInt();

    /**
     * Returns the long value of this node.
     *
     * @return The long value of this node
     *
     * @throws IncorrectTypeException When this node is not a number node
     */
    long asLong();

    /**
     * Returns the float value of this node.
     *
     * @return The float value of this node
     *
     * @throws IncorrectTypeException When this node is not a number node
     */
    float asFloat();

    /**
     * Returns the double value of this node.
     *
     * @return The double value of this node
     *
     * @throws IncorrectTypeException When this node is not a number node
     */
    double asDouble();

    /**
     * Returns the {@link BigInteger} value of this node.
     *
     * @return The {@link BigInteger} value of this node
     *
     * @throws IncorrectTypeException When this node is not a number node
     */
    BigInteger asBigInteger();

    /**
     * Returns the {@link BigDecimal} value of this node.
     *
     * @return The {@link BigDecimal} value of this node
     *
     * @throws IncorrectTypeException When this node is not a number node
     */
    BigDecimal asBigDecimal();

    /**
     * Returns the boolean value of this node.
     *
     * @return The boolean value of this node
     *
     * @throws IncorrectTypeException When this node is not a boolean node
     */
    boolean asBoolean();

    /**
     * Returns the string array value of this node.
     *
     * @return The string array value of this node
     *
     * @throws IncorrectTypeException When this node is not an array node or when one of the array's elements is not a
     *                                string node
     */
    String[] asStringArray();

    /**
     * Returns the byte array value of this node.
     *
     * @return The byte array value of this node
     *
     * @throws IncorrectTypeException When this node is not an array node or when one of the array's elements is not a
     *                                number node
     */
    byte[] asByteArray();

    /**
     * Returns the short array value of this node.
     *
     * @return The short array value of this node
     *
     * @throws IncorrectTypeException When this node is not an array node or when one of the array's elements is not a
     *                                number node
     */
    short[] asShortArray();

    /**
     * Returns the int array value of this node.
     *
     * @return The int array value of this node
     *
     * @throws IncorrectTypeException When this node is not an array node or when one of the array's elements is not a
     *                                number node
     */
    int[] asIntArray();

    /**
     * Returns the long array value of this node.
     *
     * @return The long array value of this node
     *
     * @throws IncorrectTypeException When this node is not an array node or when one of the array's elements is not a
     *                                number node
     */
    long[] asLongArray();

    /**
     * Returns the float array value of this node.
     *
     * @return The float array value of this node
     *
     * @throws IncorrectTypeException When this node is not an array node or when one of the array's elements is not a
     *                                number node
     */
    float[] asFloatArray();

    /**
     * Returns the double array value of this node.
     *
     * @return The double array value of this node
     *
     * @throws IncorrectTypeException When this node is not an array node or when one of the array's elements is not a
     *                                number node
     */
    double[] asDoubleArray();

    /**
     * Returns the {@link BigInteger} array value of this node.
     *
     * @return The {@link BigInteger} array value of this node
     *
     * @throws IncorrectTypeException When this node is not an array node or when one of the array's elements is not a
     *                                number node
     */
    BigInteger[] asBigIntegerArray();

    /**
     * Returns the {@link BigDecimal} array value of this node.
     *
     * @return The {@link BigDecimal} array value of this node
     *
     * @throws IncorrectTypeException When this node is not an array node or when one of the array's elements is not a
     *                                number node
     */
    BigDecimal[] asBigDecimalArray();

    /**
     * Returns the boolean array value of this node.
     *
     * @return The boolean array value of this node
     *
     * @throws IncorrectTypeException When this node is not an array node or when one of the array's elements is not a
     *                                boolean node
     */
    boolean[] asBooleanArray();

    /**
     * Returns the string array value of this node, enforcing the specified length. An exception is thrown if any other
     * length of array is present.
     *
     * @param fixedLength The required length of the array
     * @return The string array value of this node
     *
     * @throws IncorrectTypeException        When this node is not an array node or when one of the array's elements is
     *                                       not a string node
     * @throws IncorrectArrayLengthException When the actual length of this array node is not the required length
     */
    String[] asStringArray(int fixedLength);

    /**
     * Returns the byte array value of this node, enforcing the specified length. An exception is thrown if any other
     * length of array is present.
     *
     * @param fixedLength The required length of the array
     * @return The byte array value of this node
     *
     * @throws IncorrectTypeException        When this node is not an array node or when one of the array's elements is
     *                                       not a number node
     * @throws IncorrectArrayLengthException When the actual length of this array node is not the required length
     */
    byte[] asByteArray(int fixedLength);

    /**
     * Returns the short array value of this node, enforcing the specified length. An exception is thrown if any other
     * length of array is present.
     *
     * @param fixedLength The required length of the array
     * @return The short array value of this node
     *
     * @throws IncorrectTypeException        When this node is not an array node or when one of the array's elements is
     *                                       not a number node
     * @throws IncorrectArrayLengthException When the actual length of this array node is not the required length
     */
    short[] asShortArray(int fixedLength);

    /**
     * Returns the int array value of this node, enforcing the specified length. An exception is thrown if any other
     * length of array is present.
     *
     * @param fixedLength The required length of the array
     * @return The int array value of this node
     *
     * @throws IncorrectTypeException        When this node is not an array node or when one of the array's elements is
     *                                       not a number node
     * @throws IncorrectArrayLengthException When the actual length of this array node is not the required length
     */
    int[] asIntArray(int fixedLength);

    /**
     * Returns the long array value of this node, enforcing the specified length. An exception is thrown if any other
     * length of array is present.
     *
     * @param fixedLength The required length of the array
     * @return The long array value of this node
     *
     * @throws IncorrectTypeException        When this node is not an array node or when one of the array's elements is
     *                                       not a number node
     * @throws IncorrectArrayLengthException When the actual length of this array node is not the required length
     */
    long[] asLongArray(int fixedLength);

    /**
     * Returns the float array value of this node, enforcing the specified length. An exception is thrown if any other
     * length of array is present.
     *
     * @param fixedLength The required length of the array
     * @return The float array value of this node
     *
     * @throws IncorrectTypeException        When this node is not an array node or when one of the array's elements is
     *                                       not a number node
     * @throws IncorrectArrayLengthException When the actual length of this array node is not the required length
     */
    float[] asFloatArray(int fixedLength);

    /**
     * Returns the double array value of this node, enforcing the specified length. An exception is thrown if any other
     * length of array is present.
     *
     * @param fixedLength The required length of the array
     * @return The double array value of this node
     *
     * @throws IncorrectTypeException        When this node is not an array node or when one of the array's elements is
     *                                       not a number node
     * @throws IncorrectArrayLengthException When the actual length of this array node is not the required length
     */
    double[] asDoubleArray(int fixedLength);

    /**
     * Returns the {@link BigInteger} array value of this node, enforcing the specified length. An exception is thrown
     * if any other length of array is present.
     *
     * @param fixedLength The required length of the array
     * @return The {@link BigInteger} array value of this node
     *
     * @throws IncorrectTypeException        When this node is not an array node or when one of the array's elements is
     *                                       not a number node
     * @throws IncorrectArrayLengthException When the actual length of this array node is not the required length
     */
    BigInteger[] asBigIntegerArray(int fixedLength);

    /**
     * Returns the {@link BigDecimal} array value of this node, enforcing the specified length. An exception is thrown
     * if any other length of array is present.
     *
     * @param fixedLength The required length of the array
     * @return The {@link BigDecimal} array value of this node
     *
     * @throws IncorrectTypeException        When this node is not an array node or when one of the array's elements is
     *                                       not a number node
     * @throws IncorrectArrayLengthException When the actual length of this array node is not the required length
     */
    BigDecimal[] asBigDecimalArray(int fixedLength);

    /**
     * Returns the boolean array value of this node, enforcing the specified length. An exception is thrown if any other
     * length of array is present.
     *
     * @param fixedLength The required length of the array
     * @return The boolean array value of this node
     *
     * @throws IncorrectTypeException        When this node is not an array node or when one of the array's elements is
     *                                       not a boolean node
     * @throws IncorrectArrayLengthException When the actual length of this array node is not the required length
     */
    boolean[] asBooleanArray(int fixedLength);

    /**
     * Returns a list containing the elements of this array node. The list is a direct copy of the array and can be
     * modified without modifying the array node itself. It does not reflect this node.
     *
     * @return A list containing the elements of this node
     *
     * @throws IncorrectTypeException When this node is not an array node
     */
    List<JsonNode> asList();

    /**
     * Returns a map containing the elements of this object node, mapped to their keys. The map is a direct copy of the
     * object and can be modified without modifying the object node itself. It does not reflect this node.
     *
     * @return A map containing the elements of this node, mapped to their keys
     *
     * @throws IncorrectTypeException When this node is not an object node
     */
    Map<String, JsonNode> asMap();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Array operations
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the element of this array at the given index. Negative indices index from the end.
     *
     * @param index The index
     * @return The element at the given index
     *
     * @throws IndexOutOfBoundsException If the index falls out of the bounds of this array
     * @throws IncorrectTypeException    If this node is not an array
     */
    JsonNode get(int index);

    /**
     * Sets the element of this array at the given index. Negative indices index from the end. A null value is converted
     * to {@link #NULL}.
     *
     * @param index The index
     * @param value The new value
     * @return This instance for chaining
     *
     * @throws IndexOutOfBoundsException If the index falls out of the bounds of this array
     * @throws IncorrectTypeException    If this node is not an array
     * @throws IllegalArgumentException  When the given {@link JsonNode} implementation is not built in
     */
    JsonNode set(int index, JsonNode value);

    /**
     * Sets the element of this array at the given index to a string. Negative indices index from the end. A null value
     * is converted to {@link #NULL}.
     *
     * @param index The index
     * @param value The new value
     * @return This instance for chaining
     *
     * @throws IndexOutOfBoundsException If the index falls out of the bounds of this array
     * @throws IncorrectTypeException    If this node is not an array
     */
    JsonNode set(int index, String value);

    /**
     * Sets the element of this array at the given index to a number. Negative indices index from the end. A null value
     * is converted to {@link #NULL}.
     *
     * @param index The index
     * @param value The new value
     * @return This instance for chaining
     *
     * @throws IndexOutOfBoundsException If the index falls out of the bounds of this array
     * @throws IncorrectTypeException    If this node is not an array
     */
    JsonNode set(int index, Number value);

    /**
     * Sets the element of this array at the given index to a boolean. Negative indices index from the end. A null value
     * is converted to {@link #NULL}.
     *
     * @param index The index
     * @param value The new value
     * @return This instance for chaining
     *
     * @throws IndexOutOfBoundsException If the index falls out of the bounds of this array
     * @throws IncorrectTypeException    If this node is not an array
     */
    JsonNode set(int index, Boolean value);

    /**
     * Adds a new element to the end of this array. A null value is converted to {@link #NULL}.
     *
     * @param value The new value
     * @return This instance for chaining
     *
     * @throws IncorrectTypeException   If this node is not an array
     * @throws IllegalArgumentException When the given {@link JsonNode} implementation is not built in
     */
    JsonNode add(JsonNode value);

    /**
     * Adds a new string element to the end of this array. A null value is converted to {@link #NULL}.
     *
     * @param value The new value
     * @return This instance for chaining
     *
     * @throws IncorrectTypeException If this node is not an array
     */
    JsonNode add(String value);

    /**
     * Adds a new number to the end of this array. A null value is converted to {@link #NULL}.
     *
     * @param value The new value
     * @return This instance for chaining
     *
     * @throws IncorrectTypeException If this node is not an array
     */
    JsonNode add(Number value);

    /**
     * Adds a new boolean to the end of this array. A null value is converted to {@link #NULL}.
     *
     * @param value The new value
     * @return This instance for chaining
     *
     * @throws IncorrectTypeException If this node is not an array
     */
    JsonNode add(Boolean value);

    /**
     * Inserts a new element at a certain position in this array, before the element at the given index. Negative
     * indices index from the end (note that index -1 does <strong>not</strong> add it to the end, but before the last
     * element). A null value is converted to {@link #NULL}.
     *
     * @param index The index to insert at
     * @param value The new value
     * @return This instance for chaining
     *
     * @throws IndexOutOfBoundsException When the given index falls out of the bounds of this array
     * @throws IncorrectTypeException    If this node is not an array
     * @throws IllegalArgumentException  When the given {@link JsonNode} implementation is not built in
     */
    JsonNode insert(int index, JsonNode value);

    /**
     * Inserts a new string element at a certain position in this array, before the element at the given index. Negative
     * indices index from the end (note that index -1 does <strong>not</strong> add it to the end, but before the last
     * element). A null value is converted to {@link #NULL}.
     *
     * @param index The index to insert at
     * @param value The new value
     * @return This instance for chaining
     *
     * @throws IndexOutOfBoundsException When the given index falls out of the bounds of this array
     * @throws IncorrectTypeException    If this node is not an array
     */
    JsonNode insert(int index, String value);

    /**
     * Inserts a new number element at a certain position in this array, before the element at the given index. Negative
     * indices index from the end (note that index -1 does <strong>not</strong> add it to the end, but before the last
     * element). A null value is converted to {@link #NULL}.
     *
     * @param index The index to insert at
     * @param value The new value
     * @return This instance for chaining
     *
     * @throws IndexOutOfBoundsException When the given index falls out of the bounds of this array
     * @throws IncorrectTypeException    If this node is not an array
     */
    JsonNode insert(int index, Number value);

    /**
     * Inserts a new boolean element at a certain position in this array, before the element at the given index.
     * Negative indices index from the end (note that index -1 does <strong>not</strong> add it to the end, but before
     * the last element). A null value is converted to {@link #NULL}.
     *
     * @param index The index to insert at
     * @param value The new value
     * @return This instance for chaining
     *
     * @throws IndexOutOfBoundsException When the given index falls out of the bounds of this array
     * @throws IncorrectTypeException    If this node is not an array
     */
    JsonNode insert(int index, Boolean value);

    /**
     * Removes the element at the given index in this array. Negative indices index from the end.
     *
     * @param index The index of the element to remove.
     * @return This instance for chaining
     *
     * @throws IndexOutOfBoundsException When the given index falls out of the bounds of this array
     * @throws IncorrectTypeException    If this node is not an array
     */
    JsonNode remove(int index);

    /**
     * Returns the size of this array or object, i.e. the amount of elements in this container.
     *
     * @return The size of this array or object
     *
     * @throws IncorrectTypeException If this is not an array or object
     */
    int size();

    /**
     * Enforces this array or object to have a specific number of elements, throwing an {@link
     * IncorrectArrayLengthException} when it is not the correct size.
     *
     * @param size The expected size of this node
     * @return This instance for chaining
     *
     * @throws IncorrectTypeException        If this is not an array or object
     * @throws IncorrectArrayLengthException When this object or array is not of the given length
     */
    JsonNode requireSize(int size);

    /**
     * Removes all elements from this array or object.
     *
     * @return This instance for chaining
     *
     * @throws IncorrectTypeException If this node is not an array or object
     */
    JsonNode clear();



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Object operations
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the element at the specified key in this object. This returns null (not {@link #NULL}) when no value has
     * been associated to this key. A null key is treated as a literal key "null".
     *
     * @param key The key to get
     * @return The node at the given key, or null
     *
     * @throws IncorrectTypeException When this node is not an object
     */
    JsonNode get(String key);

    /**
     * Replaces or adds a new element at the specified key in this object. A null value is converted to {@link #NULL}. A
     * null key is treated as a literal key "null".
     *
     * @param key   The key to set at
     * @param value The new value
     * @return This instance for chaining
     *
     * @throws IncorrectTypeException   When this node is not an object
     * @throws IllegalArgumentException When the given {@link JsonNode} implementation is not built in
     */
    JsonNode set(String key, JsonNode value);

    /**
     * Replaces or adds a new string element at the specified key in this object. A null value is converted to {@link
     * #NULL}. A null key is treated as a literal key "null".
     *
     * @param key   The key to set at
     * @param value The new value
     * @return This instance for chaining
     *
     * @throws IncorrectTypeException   When this node is not an object
     * @throws IllegalArgumentException When the given {@link JsonNode} implementation is not built in
     */
    JsonNode set(String key, String value);

    /**
     * Replaces or adds a new number element at the specified key in this object. A null value is converted to {@link
     * #NULL}. A null key is treated as a literal key "null".
     *
     * @param key   The key to set at
     * @param value The new value
     * @return This instance for chaining
     *
     * @throws IncorrectTypeException   When this node is not an object
     * @throws IllegalArgumentException When the given {@link JsonNode} implementation is not built in
     */
    JsonNode set(String key, Number value);

    /**
     * Replaces or adds a new boolean element at the specified key in this object. A null value is converted to {@link
     * #NULL}. A null key is treated as a literal key "null".
     *
     * @param key   The key to set at
     * @param value The new value
     * @return This instance for chaining
     *
     * @throws IncorrectTypeException   When this node is not an object
     * @throws IllegalArgumentException When the given {@link JsonNode} implementation is not built in
     */
    JsonNode set(String key, Boolean value);

    /**
     * Removes the element at the specified key in this object, if it was assigned. If no element was assigned, this
     * method does nothing. A null key is treated as a literal key "null".
     *
     * @param key The key to remove from this object
     * @return This instance for chaining
     *
     * @throws IncorrectTypeException When this node is not an object
     */
    JsonNode remove(String key);

    /**
     * Returns whether a key is present in this object. A null key is treated as a literal key "null".
     *
     * @param key The key to check for
     * @return Whether the key is present
     *
     * @throws IncorrectTypeException When this node is not an object
     */
    boolean has(String key);

    /**
     * Returns a set of all the keys in this object node. This set reflects this node, and removing from this set means
     * removing from this node.
     *
     * @return The set of keys in this object
     *
     * @throws IncorrectTypeException When this node is not an object
     */
    Set<String> keys();

    /**
     * Loops over all key-value pairs in this object, passing them into the given consumer function.
     *
     * @param fn The function to call for each key-value pair
     * @throws IncorrectTypeException When this node is not an object
     * @throws NullPointerException   When the given function is null
     */
    void forEachEntry(BiConsumer<? super String, ? super JsonNode> fn);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Querying
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Queries this JSON tree with the given path string, see {@link JsonPath#parse(String)} for the syntax of this
     * path. This method will return null (not {@link #NULL}) when the last query was on an object that did not contain
     * the queried key.
     *
     * @param path The path to query by
     * @return The node that was navigated to, or null
     *
     * @throws IncorrectTypeException    When trying to query a key from a non-object or an index from a non-array.
     * @throws IndexOutOfBoundsException When querying an array at an out-of-range index
     * @throws NoSuchElementException    When trying to query on a nonexisting value (i.e. {@code a.b} would try to
     *                                   query {@code b} from {@code a} while {@code a} was not found in root)
     * @throws NullPointerException      When the given path is null
     */
    JsonNode query(String path);

    /**
     * Queries this JSON tree with the given path. This method will return null (not {@link #NULL}) when the last query
     * was on an object that did not contain the queried key.
     *
     * @param path The path to query by
     * @return The node that was navigated to, or null
     *
     * @throws IncorrectTypeException    When trying to query a key from a non-object or an index from a non-array.
     * @throws IndexOutOfBoundsException When querying an array at an out-of-range index
     * @throws NoSuchElementException    When trying to query on a nonexisting value (i.e. {@code a.b} would try to
     *                                   query {@code b} from {@code a} while {@code a} was not found in root)
     * @throws NullPointerException      When the given path is null
     */
    JsonNode query(JsonPath path);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Misc
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a deep copy of this tree. If this is a primitive (string, number, boolean or null), this will return
     * itself (since these instances are immutable). If this is an array, it will return a new array with a deep copy of
     * each element in the same order. If this is an object, it will return a new object with a deep copy of each
     * element, under their respective keys and in the same order.
     *
     * @return The deep copy of this tree
     */
    JsonNode deepCopy();

    /**
     * Returns a shallow copy of this tree. If this is a primitive (string, number, boolean or null), this will return
     * itself (since these instances are immutable). If this is an array, it will return a new array with the same nodes
     * in the same order. If this is an object, it will return a new object with the same nodes under their respective
     * keys, and in the same order.
     *
     * @return The shallow copy of this tree
     */
    JsonNode copy();
}
