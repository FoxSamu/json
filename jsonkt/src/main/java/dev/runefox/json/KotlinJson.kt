@file:Suppress("unused")

package dev.runefox.json

import dev.runefox.json.codec.JsonCodec
import dev.runefox.json.impl.KotlinNumberWrapper
import dev.runefox.json.impl.LazyParseNumber
import dev.runefox.json.impl.LazyParseRadix
import java.math.BigDecimal
import java.math.BigInteger

/**
 * The JSON value `null`
 */
val JSON_NULL: JsonNode = JsonNode.NULL

fun JsonNode?.asJson() = when (this) {
    null -> JSON_NULL
    else -> this
}

fun <T> T?.asJson(codec: JsonCodec<T>): JsonNode = if (this == null) JSON_NULL else codec.encode(this)
fun Map<String, JsonNode>.asJson(): JsonNode = JsonNode.`object`(this)
fun Iterable<JsonNode>.asJson(): JsonNode = JsonNode.array(this)

/**
 * The JSON value `0`
 */
val JSON_ZERO: JsonNode = JsonNode.ZERO

fun Number.asJson() = jsonNumber(this)
fun UByte.asJson() = jsonNumber(this)
fun UShort.asJson() = jsonNumber(this)
fun UInt.asJson() = jsonNumber(this)
fun ULong.asJson() = jsonNumber(this)

/**
 * The JSON value `false`
 */
val JSON_FALSE: JsonNode = JsonNode.FALSE

/**
 * The JSON value `true`
 */
val JSON_TRUE: JsonNode = JsonNode.TRUE

fun Boolean.asJson() = if (this) JSON_TRUE else JSON_FALSE

/**
 * The JSON value `""`
 */
val JSON_EMPTY_STRING: JsonNode = JsonNode.EMPTY_STRING

fun String.asJson() = jsonString(this)

/**
 * Tests whether the JSON node is of the given type.
 */
infix fun JsonNode.isType(type: NodeType): Boolean = this.`is`(type)

/**
 * Tests whether the JSON node is of one of the given types.
 */
fun JsonNode.isType(vararg type: NodeType): Boolean = this.`is`(*type)

/**
 * Tests whether the JSON node is of this type.
 */
operator fun NodeType.contains(node: JsonNode): Boolean {
    return node isType this
}

/**
 * Tests if the given key is present in this JSON object.
 * @param key The key being looked for.
 *
 * @throws IncorrectTypeException If this node is not of type object.
 */
operator fun JsonNode.contains(key: String): Boolean {
    return this.has(key)
}

/**
 * Tests if the given index is present in this JSON array.
 * @param index The index being looked for.
 *
 * @throws IncorrectTypeException If this node is not of type array.
 */
operator fun JsonNode.contains(index: Int): Boolean {
    requireArray()
    return index in (0..<size())
}

/**
 * Returns a new JSON array with the elements of this array at the indices of the given progression.
 * Out of range indices are ignored.
 *
 * @throws IncorrectTypeException If this node is not of type array.
 */
operator fun JsonNode.get(range: IntProgression): JsonNode {
    requireArray()
    val arr = jsonArray()
    for (i in range) {
        if (i in this)
            arr += this[i]
    }
    return arr
}

/**
 * Creates a JSON string value of the given Kotlin string value. Returns JSON `null` when the argument is `null`.
 */
fun jsonString(value: String?): JsonNode = JsonNode.string(value)

/**
 * Creates a JSON number value of the given Kotlin number value. Returns JSON `null` when the argument is `null`.
 */
fun jsonNumber(value: Number?): JsonNode = JsonNode.number(value)

/**
 * Creates a JSON boolean value of the given Kotlin boolean value. Returns JSON `null` when the argument is `null`.
 */
fun jsonBool(value: Boolean?): JsonNode = JsonNode.bool(value)

/**
 * Creates an empty JSON object: `{}`.
 */
fun jsonObject(): JsonNode = JsonNode.`object`()

/**
 * Creates an empty JSON array: `[]`.
 */
fun jsonArray(): JsonNode = JsonNode.array()

/**
 * Runs given function on this node and returns itself. Useful for constructs like:
 * ```kotlin
 * return arr {
 *     it += 3
 * }
 * ```
 */
operator fun JsonNode.invoke(config: (JsonNode) -> Unit): JsonNode {
    config(this)
    return this
}

/**
 * Enforces this array or object to have an amount of elements in the given range, throwing an
 * [IncorrectSizeException] when it is not the correct size.
 *
 * @param range The expected size range of this node
 * @return This instance for chaining
 *
 * @throws IncorrectTypeException If this is not an array or object
 * @throws IncorrectSizeException When this object or array is not of the required length
 */
fun JsonNode.requireSize(range: IntRange): JsonNode {
    return requireSize(range.first, range.last - 1)
}

/**
 * Creates an empty JSON object and runs the given function on it. Useful to initialize a JSON object:
 * ```kotlin
 * return jsonObject {
 *     it["x"] = true
 *     it["y"] = false
 * }
 * ```
 */
fun jsonObject(config: (JsonNode) -> Unit): JsonNode {
    return jsonObject().invoke(config)
}

/**
 * Creates an empty JSON array and runs the given function on it. Useful to initialize a JSON array:
 * ```kotlin
 * return jsonArray {
 *     it += true
 *     it += false
 * }
 * ```
 */
fun jsonArray(config: (JsonNode) -> Unit): JsonNode {
    return jsonArray().invoke(config)
}

/**
 * Creates a JSON array containing the given elements.
 * @param nodes The elements in the array. Any null values are replaced by JSON null values.
 * @return The newly created array.
 */
fun jsonArray(vararg nodes: JsonNode?): JsonNode = JsonNode.array(*nodes)

/**
 * Creates a JSON array containing the given elements.
 * @param nodes The elements in the array.
 * @return The newly created array.
 */
fun jsonArray(vararg nodes: Int): JsonNode = JsonNode.numberArray(nodes)

/**
 * Creates a JSON array containing the given elements.
 * @param nodes The elements in the array.
 * @return The newly created array.
 */
fun jsonArray(vararg nodes: Byte): JsonNode = JsonNode.numberArray(nodes)

/**
 * Creates a JSON array containing the given elements.
 * @param nodes The elements in the array.
 * @return The newly created array.
 */
fun jsonArray(vararg nodes: Short): JsonNode = JsonNode.numberArray(nodes)

/**
 * Creates a JSON array containing the given elements.
 * @param nodes The elements in the array.
 * @return The newly created array.
 */
fun jsonArray(vararg nodes: Long): JsonNode = JsonNode.numberArray(nodes)

/**
 * Creates a JSON array containing the given elements.
 * @param nodes The elements in the array.
 */
fun jsonArray(vararg nodes: Float): JsonNode = JsonNode.numberArray(nodes)

/**
 * Creates a JSON array containing the given elements.
 * @param nodes The elements in the array.
 * @return The newly created array.
 */
fun jsonArray(vararg nodes: Double): JsonNode = JsonNode.numberArray(nodes)

/**
 * Creates a JSON array containing the given elements.
 * @param nodes The elements in the array.
 * @return The newly created array.
 */
fun jsonArray(vararg nodes: Number): JsonNode = JsonNode.numberArray(*nodes)

/**
 * Creates a JSON array containing the given elements.
 * @param nodes The elements in the array. Any null values are replaced by JSON null values.
 * @return The newly created array.
 */
fun jsonArray(vararg nodes: String?): JsonNode = JsonNode.stringArray(*nodes)

/**
 * Creates a JSON array containing the given elements.
 * @param nodes The elements in the array.
 * @return The newly created array.
 */
fun jsonArray(vararg nodes: Boolean): JsonNode = JsonNode.boolArray(nodes)

/**
 * Creates a JSON array containing the given elements.
 * @param nodes The elements in the array. Any null values are replaced by JSON null values.
 * @return The newly created array.
 */
fun jsonArray(vararg nodes: Boolean?): JsonNode = JsonNode.boolArray(*nodes)

/**
 * Creates a JSON array containing the elements in the given iterable.
 * @param nodes The elements in the array. Any null values are replaced by JSON null values.
 * @return The newly created array.
 */
fun jsonArray(nodes: Iterable<JsonNode?>?): JsonNode = when (nodes) {
    null -> JsonNode.NULL
    else -> JsonNode.array(nodes)
}

private class KtArrayCodec<A>(val codec: JsonCodec<A>, val factory: (Int) -> Array<A>) : JsonCodec<Array<A>> {
    override fun encode(obj: Array<A>): JsonNode {
        val arr = jsonArray()
        for (x in obj) arr.add(codec.encode(x))
        return arr
    }

    override fun decode(json: JsonNode): Array<A> {
        json.requireArray()

        val arr = factory(json.size())
        for (i in 0..<json.size())
            arr[i] = codec.decode(json[i])
        return arr
    }
}

private class KtSequenceCodec<A>(val codec: JsonCodec<A>) : JsonCodec<Sequence<A>> {
    override fun encode(obj: Sequence<A>): JsonNode {
        val arr = jsonArray()
        for (x in obj) arr.add(codec.encode(x))
        return arr
    }

    override fun decode(json: JsonNode): Sequence<A> {
        json.requireArray()

        val arr = mutableListOf<A>()
        for (i in 0..<json.size())
            arr += codec.decode(json[i])
        return arr.asSequence()
    }
}

/**
 * Creates a [JsonCodec] that encodes and decodes [Array]s of this codec.
 * @return The created codec.
 */
fun <A> JsonCodec<A>.arrayOf(factory: (Int) -> Array<A>): JsonCodec<Array<A>> {
    return KtArrayCodec(this, factory)
}

/**
 * Creates a [JsonCodec] that encodes and decodes [Sequence]s of this codec.
 * @return The created codec.
 */
fun <A> JsonCodec<A>.sequenceOf(): JsonCodec<Sequence<A>> {
    return KtSequenceCodec(this)
}

/**
 * Encodes this object to a [JsonNode] using the given codec.
 * @return The encoded object.
 */
infix fun <A> A.encoded(codec: JsonCodec<A>): JsonNode = codec.encode(this)

/**
 * Decodes this [JsonNode] to an object using the given codec.
 * @return The decoded object.
 */
infix fun <A> JsonNode.decoded(codec: JsonCodec<A>): A = codec.decode(this)

/**
 * Adds an element to a list.
 * @param value The element to add. Null values will be inserted as JSON null values.
 * @throws IncorrectTypeException When this node is not an array node.
 */
operator fun JsonNode.plusAssign(value: Boolean?) {
    add(value)
}

/**
 * Adds an element to a list.
 * @param value The element to add. Null values will be inserted as JSON null values.
 * @throws IncorrectTypeException When this node is not an array node.
 */
operator fun JsonNode.plusAssign(value: Number?) {
    add(value)
}

/**
 * Adds an element to a list.
 * @param value The element to add. Null values will be inserted as JSON null values.
 * @throws IncorrectTypeException When this node is not an array node.
 */
operator fun JsonNode.plusAssign(value: String?) {
    add(value)
}

/**
 * Adds an element to a list.
 * @param value The element to add. Null values will be inserted as JSON null values.
 * @throws IncorrectTypeException When this node is not an array node.
 */
operator fun JsonNode.plusAssign(value: JsonNode?) {
    add(value)
}

/**
 * Wraps this node into a new array node: `[this]`, and runs the given configuration function on it.
 * Can be used like:
 * ```kotlin
 * node = node.wrap {
 *     it += 3
 * }
 * ```
 * @param config The configuration function.
 * @return The new array node.
 */
fun JsonNode.wrap(config: (JsonNode) -> Unit): JsonNode {
    return (wrap())(config)
}

/**
 * Wraps this node into a new object node under the given key: `{key: this}`, and runs the given configuration function
 * on it.
 * Can be used like:
 * ```kotlin
 * node = node.wrap("x") {
 *     it["y"] = 3
 * }
 * ```
 * @param config The configuration function.
 * @return The new object node.
 */
fun JsonNode.wrap(key: String, config: (JsonNode) -> Unit): JsonNode {
    return (wrap(key))(config)
}

/**
 * Creates a shallow copy of this node, and runs the given configuration function on it.
 * Can be used like:
 * ```kotlin
 * node = node.copy {
 *     it += 3
 * }
 * ```
 * @param config The configuration function.
 * @return The copied node.
 */
fun JsonNode.copy(config: (JsonNode) -> Unit): JsonNode {
    return (copy())(config)
}

/**
 * Creates a deep copy of this node, and runs the given configuration function on it.
 * Can be used like:
 * ```kotlin
 * node = node.deepCopy {
 *     it += 3
 * }
 * ```
 * @param config The configuration function.
 * @return The copied node.
 */
fun JsonNode.deepCopy(config: (JsonNode) -> Unit): JsonNode {
    return (deepCopy())(config)
}

/**
 * The set of keys of this object node.
 * @throws IncorrectTypeException When the given node is not an object node.
 */
val JsonNode.keys: Set<String> get() = keySet()

/**
 * The set of entries of this object node.
 * @throws IncorrectTypeException When the given node is not an object node.
 */
val JsonNode.entries: Set<Map.Entry<String, JsonNode>> get() = entrySet()

/**
 * The set of values of this object or array node.
 * @throws IncorrectTypeException When the given node is not an object or array node.
 */
val JsonNode.values: Collection<JsonNode> get() = values()

/**
 * The size of this object or array node.
 * @throws IncorrectTypeException When the given node is not an object or array node.
 */
val JsonNode.size: Int get() = size()

/**
 * The size of this string, object or array node.
 * @throws IncorrectTypeException When the given node is not a string, object or array node.
 */
val JsonNode.length: Int get() = length()

/**
 * Returns an [UByte] value of this node.
 * @return The [UByte] value of this node
 * @throws IncorrectTypeException When this node is not a number node
 */
fun JsonNode.asUByte(): UByte {
    val t = asNumber()
    if (t is UByteWrapper)
        return t.wrap
    if (t is LazyParseNumber)
        return t.unsignedLongValue().toUByte()
    if (t is LazyParseRadix)
        return t.toLong().toUByte()
    return asByte().toUByte()
}

/**
 * Returns an [UShort] value of this node.
 * @return The [UShort] value of this node
 * @throws IncorrectTypeException When this node is not a number node
 */
fun JsonNode.asUShort(): UShort {
    val t = asNumber()
    if (t is UShortWrapper)
        return t.wrap
    if (t is LazyParseNumber)
        return t.unsignedLongValue().toUShort()
    if (t is LazyParseRadix)
        return t.toLong().toUShort()
    return asShort().toUShort()
}

/**
 * Returns an [UInt] value of this node.
 * @return The [UInt] value of this node
 * @throws IncorrectTypeException When this node is not a number node
 */
fun JsonNode.asUInt(): UInt {
    val t = asNumber()
    if (t is UIntWrapper)
        return t.wrap
    if (t is LazyParseNumber)
        return t.unsignedLongValue().toUInt()
    if (t is LazyParseRadix)
        return t.toLong().toUInt()
    return asInt().toUInt()
}

/**
 * Returns an [ULong] value of this node.
 * @return The [ULong] value of this node
 * @throws IncorrectTypeException When this node is not a number node
 */
fun JsonNode.asULong(): ULong {
    val t = asNumber()
    if (t is ULongWrapper)
        return t.wrap
    if (t is LazyParseNumber)
        return t.unsignedLongValue().toULong()
    if (t is LazyParseRadix)
        return t.toLong().toULong()
    return asLong().toULong()
}


/**
 * Creates a JSON number value of the given Kotlin number value. Returns JSON `null` when the argument is `null`.
 */
fun jsonNumber(value: UByte?): JsonNode = when (value) {
    null -> JsonNode.NULL
    else -> JsonNode.number(UByteWrapper(value))
}

/**
 * Creates a JSON number value of the given Kotlin number value. Returns JSON `null` when the argument is `null`.
 */
fun jsonNumber(value: UShort?): JsonNode = when (value) {
    null -> JsonNode.NULL
    else -> JsonNode.number(UShortWrapper(value))
}

/**
 * Creates a JSON number value of the given Kotlin number value. Returns JSON `null` when the argument is `null`.
 */
fun jsonNumber(value: UInt?): JsonNode = when (value) {
    null -> JsonNode.NULL
    else -> JsonNode.number(UIntWrapper(value))
}

/**
 * Creates a JSON number value of the given Kotlin number value. Returns JSON `null` when the argument is `null`.
 */
fun jsonNumber(value: ULong?): JsonNode = when (value) {
    null -> JsonNode.NULL
    else -> JsonNode.number(ULongWrapper(value))
}


/**
 * Adds a new element to the end of this array. A null value is converted to a JSON null.
 * @param value The new value
 * @return This instance for chaining
 * @throws IncorrectTypeException   If this node is not an array
 */
fun JsonNode.add(value: UByte?): JsonNode = add(jsonNumber(value))

/**
 * Adds a new element to the end of this array. A null value is converted to a JSON null.
 * @param value The new value
 * @return This instance for chaining
 * @throws IncorrectTypeException   If this node is not an array
 */
fun JsonNode.add(value: UShort?): JsonNode = add(jsonNumber(value))

/**
 * Adds a new element to the end of this array. A null value is converted to a JSON null.
 * @param value The new value
 * @return This instance for chaining
 * @throws IncorrectTypeException   If this node is not an array
 */
fun JsonNode.add(value: UInt?): JsonNode = add(jsonNumber(value))

/**
 * Adds a new element to the end of this array. A null value is converted to a JSON null.
 * @param value The new value
 * @return This instance for chaining
 * @throws IncorrectTypeException   If this node is not an array
 */
fun JsonNode.add(value: ULong?): JsonNode = add(jsonNumber(value))

/**
 * Sets the element of this array at the given index to a number. Negative indices index from the end. A null value
 * is converted to a JSON null.
 *
 * @param index The index
 * @param value The new value
 * @return This instance for chaining
 *
 * @throws IndexOutOfBoundsException If the index falls out of the bounds of this array
 * @throws IncorrectTypeException    If this node is not an array
 */
operator fun JsonNode.set(index: Int, value: UByte?): JsonNode = set(index, jsonNumber(value))

/**
 * Sets the element of this array at the given index to a number. Negative indices index from the end. A null value
 * is converted to a JSON null.
 *
 * @param index The index
 * @param value The new value
 * @return This instance for chaining
 *
 * @throws IndexOutOfBoundsException If the index falls out of the bounds of this array
 * @throws IncorrectTypeException    If this node is not an array
 */
operator fun JsonNode.set(index: Int, value: UShort?): JsonNode = set(index, jsonNumber(value))

/**
 * Sets the element of this array at the given index to a number. Negative indices index from the end. A null value
 * is converted to a JSON null.
 *
 * @param index The index
 * @param value The new value
 * @return This instance for chaining
 *
 * @throws IndexOutOfBoundsException If the index falls out of the bounds of this array
 * @throws IncorrectTypeException    If this node is not an array
 */
operator fun JsonNode.set(index: Int, value: UInt?): JsonNode = set(index, jsonNumber(value))

/**
 * Sets the element of this array at the given index to a number. Negative indices index from the end. A null value
 * is converted to a JSON null.
 *
 * @param index The index
 * @param value The new value
 * @return This instance for chaining
 *
 * @throws IndexOutOfBoundsException If the index falls out of the bounds of this array
 * @throws IncorrectTypeException    If this node is not an array
 */
operator fun JsonNode.set(index: Int, value: ULong?): JsonNode = set(index, jsonNumber(value))

/**
 * Replaces or adds a new number element at the specified key in this object. A null value is converted to
 * a JSON null. A null key is treated as a literal key "null".
 *
 * @param key   The key to set at
 * @param value The new value
 * @return This instance for chaining
 *
 * @throws IncorrectTypeException   When this node is not an object
 */
operator fun JsonNode.set(key: String, value: UByte?): JsonNode = set(key, jsonNumber(value))

/**
 * Replaces or adds a new number element at the specified key in this object. A null value is converted to
 * a JSON null. A null key is treated as a literal key "null".
 *
 * @param key   The key to set at
 * @param value The new value
 * @return This instance for chaining
 *
 * @throws IncorrectTypeException   When this node is not an object
 */
operator fun JsonNode.set(key: String, value: UShort?): JsonNode = set(key, jsonNumber(value))

/**
 * Replaces or adds a new number element at the specified key in this object. A null value is converted to
 * a JSON null. A null key is treated as a literal key "null".
 *
 * @param key   The key to set at
 * @param value The new value
 * @return This instance for chaining
 *
 * @throws IncorrectTypeException   When this node is not an object
 */
operator fun JsonNode.set(key: String, value: UInt?): JsonNode = set(key, jsonNumber(value))

/**
 * Replaces or adds a new number element at the specified key in this object. A null value is converted to
 * a JSON null. A null key is treated as a literal key "null".
 *
 * @param key   The key to set at
 * @param value The new value
 * @return This instance for chaining
 *
 * @throws IncorrectTypeException   When this node is not an object
 */
operator fun JsonNode.set(key: String, value: ULong?): JsonNode = set(key, jsonNumber(value))


/**
 * Adds an element to a list.
 * @param value The element to add. Null values will be inserted as JSON null values.
 * @throws IncorrectTypeException When this node is not an array node.
 */
operator fun JsonNode.plusAssign(value: UByte?) {
    add(value)
}


/**
 * Adds an element to a list.
 * @param value The element to add. Null values will be inserted as JSON null values.
 * @throws IncorrectTypeException When this node is not an array node.
 */
operator fun JsonNode.plusAssign(value: UShort?) {
    add(value)
}


/**
 * Adds an element to a list.
 * @param value The element to add. Null values will be inserted as JSON null values.
 * @throws IncorrectTypeException When this node is not an array node.
 */
operator fun JsonNode.plusAssign(value: UInt?) {
    add(value)
}


/**
 * Adds an element to a list.
 * @param value The element to add. Null values will be inserted as JSON null values.
 * @throws IncorrectTypeException When this node is not an array node.
 */
operator fun JsonNode.plusAssign(value: ULong?) {
    add(value)
}


/**
 * A [JsonCodec] that encodes and decodes [UByte]s.
 */
val UBYTE: JsonCodec<UByte> = JsonCodec.of({ jsonNumber(it) }, JsonNode::asUByte)

/**
 * A [JsonCodec] that encodes and decodes [UShort]s.
 */
val USHORT: JsonCodec<UShort> = JsonCodec.of({ jsonNumber(it) }, JsonNode::asUShort)

/**
 * A [JsonCodec] that encodes and decodes [UInt]s.
 */
val UINT: JsonCodec<UInt> = JsonCodec.of({ jsonNumber(it) }, JsonNode::asUInt)

/**
 * A [JsonCodec] that encodes and decodes [ULong]s.
 */
val ULONG: JsonCodec<ULong> = JsonCodec.of({ jsonNumber(it) }, JsonNode::asULong)


/**
 * A [JsonCodec] that encodes and decodes [IntRange]s as objects `{"from": ..., "to": ...}` where `to` is inclusive.
 */
val INT_RANGE: JsonCodec<IntRange> = JsonCodec.INT.closedRangeOf { x, y -> x..y }

/**
 * A [JsonCodec] that encodes and decodes [LongRange]s as objects `{"from": ..., "to": ...}` where `to` is inclusive.
 */
val LONG_RANGE: JsonCodec<LongRange> = JsonCodec.LONG.closedRangeOf { x, y -> x..y }

/**
 * A [JsonCodec] that encodes and decodes [UIntRange]s as objects `{"from": ..., "to": ...}` where `to` is inclusive.
 */
val UINT_RANGE: JsonCodec<UIntRange> = UINT.closedRangeOf { x, y -> x..y }

/**
 * A [JsonCodec] that encodes and decodes [ULongRange]s as objects `{"from": ..., "to": ...}` where `to` is inclusive.
 */
val ULONG_RANGE: JsonCodec<ULongRange> = ULONG.closedRangeOf { x, y -> x..y }

/**
 * A [JsonCodec] that encodes and decodes [IntRange]s as objects `{"from": ..., "to": ...}` where `to` is exclusive.
 */
val INT_RANGE_OPEN: JsonCodec<IntRange> = JsonCodec.INT.openEndRangeOf { x, y -> x..<y }

/**
 * A [JsonCodec] that encodes and decodes [LongRange]s as objects `{"from": ..., "to": ...}` where `to` is exclusive.
 */
val LONG_RANGE_OPEN: JsonCodec<LongRange> = JsonCodec.LONG.openEndRangeOf { x, y -> x..<y }

/**
 * A [JsonCodec] that encodes and decodes [UIntRange]s as objects `{"from": ..., "to": ...}` where `to` is exclusive.
 */
val UINT_RANGE_OPEN: JsonCodec<UIntRange> = UINT.openEndRangeOf { x, y -> x..<y }

/**
 * A [JsonCodec] that encodes and decodes [ULongRange]s as objects `{"from": ..., "to": ...}` where `to` is exclusive.
 */
val ULONG_RANGE_OPEN: JsonCodec<ULongRange> = ULONG.openEndRangeOf { x, y -> x..<y }

/**
 * Creates a codec that encodes [ClosedRange]s of the elements en-/decoded by this codec, in the format
 * `{"from": ..., "to": ...}`.
 */
fun <T : Comparable<T>, R : ClosedRange<T>> JsonCodec<T>.closedRangeOf(factory: (T, T) -> R): JsonCodec<R> {
    return JsonCodec.of({ r: ClosedRange<T> ->
        jsonObject {
            it["from"] = r.start encoded this
            it["to"] = r.endInclusive encoded this
        }
    }, {
        it.requireObject().requireHas("from").requireHas("to")
        factory(it["from"] decoded this, it["to"] decoded this)
    })
}

/**
 * Creates a codec that encodes [OpenEndRange]s of the elements en-/decoded by this codec, in the format
 * `{"from": ..., "to": ...}`.
 */
fun <T : Comparable<T>, R : OpenEndRange<T>> JsonCodec<T>.openEndRangeOf(factory: (T, T) -> R): JsonCodec<R> {
    return JsonCodec.of({ r: OpenEndRange<T> ->
        jsonObject {
            it["from"] = r.start encoded this
            it["to"] = r.endExclusive encoded this
        }
    }, {
        it.requireObject().requireHas("from").requireHas("to")
        factory(it["from"] decoded this, it["to"] decoded this)
    })
}


// Wrappers for unsigned number types. These wrappers represent the original values as Numbers, which are then
// correctly handled by the library

private data class UByteWrapper(val wrap: UByte) : KotlinNumberWrapper() {
    override fun toByte(): Byte = wrap.toByte()
    override fun toInt(): Int = wrap.toInt()
    override fun toLong(): Long = wrap.toLong()
    override fun toShort(): Short = wrap.toShort()
    override fun toDouble(): Double = wrap.toDouble()
    override fun toFloat(): Float = wrap.toFloat()
    override fun toString(): String = wrap.toString()
    override fun represent(): String = toString()

    override fun toBigInteger(): BigInteger = BigInteger(toString())
    override fun toBigDecimal(): BigDecimal = BigDecimal(toString())
}

private data class UShortWrapper(val wrap: UShort) : KotlinNumberWrapper() {
    override fun toByte(): Byte = wrap.toByte()
    override fun toDouble(): Double = wrap.toDouble()
    override fun toFloat(): Float = wrap.toFloat()
    override fun toInt(): Int = wrap.toInt()
    override fun toLong(): Long = wrap.toLong()
    override fun toShort(): Short = wrap.toShort()
    override fun toString(): String = wrap.toString()
    override fun represent(): String = toString()

    override fun toBigInteger(): BigInteger = BigInteger(toString())
    override fun toBigDecimal(): BigDecimal = BigDecimal(toString())
}

private data class UIntWrapper(val wrap: UInt) : KotlinNumberWrapper() {
    override fun toByte(): Byte = wrap.toByte()
    override fun toDouble(): Double = wrap.toDouble()
    override fun toFloat(): Float = wrap.toFloat()
    override fun toInt(): Int = wrap.toInt()
    override fun toLong(): Long = wrap.toLong()
    override fun toShort(): Short = wrap.toShort()
    override fun toString(): String = wrap.toString()
    override fun represent(): String = toString()

    override fun toBigInteger(): BigInteger = BigInteger(toString())
    override fun toBigDecimal(): BigDecimal = BigDecimal(toString())
}

private data class ULongWrapper(val wrap: ULong) : KotlinNumberWrapper() {
    override fun toByte(): Byte = wrap.toByte()
    override fun toDouble(): Double = wrap.toDouble()
    override fun toFloat(): Float = wrap.toFloat()
    override fun toInt(): Int = wrap.toInt()
    override fun toLong(): Long = wrap.toLong()
    override fun toShort(): Short = wrap.toShort()
    override fun toString(): String = wrap.toString()
    override fun represent(): String = toString()

    override fun toBigInteger(): BigInteger = BigInteger(toString())
    override fun toBigDecimal(): BigDecimal = BigDecimal(toString())
}
