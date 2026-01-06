/*
 * Copyright 2022-2026 O. W. Nankman
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "
 * AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

@file:Suppress("unused")

package dev.runefox.json

import dev.runefox.json.codec.JsonCodec
import dev.runefox.json.impl.KotlinUnsignedIntWrapper
import dev.runefox.json.impl.UnparsedHexNumber
import dev.runefox.json.impl.UnparsedNumber
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.MonthDay
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.Year
import java.time.YearMonth
import java.time.ZonedDateTime
import java.util.UUID

/**
 * The JSON value `null`
 */
val JsonNull: JsonNode = JsonNode.NULL

/**
 * The JSON value `0`
 */
val JsonZero: JsonNode = JsonNode.ZERO

/**
 * The JSON value `false`
 */
val JsonFalse: JsonNode = JsonNode.FALSE

/**
 * The JSON value `true`
 */
val JsonTrue: JsonNode = JsonNode.TRUE

/**
 * The JSON value `""`
 */
val JsonEmptyString: JsonNode = JsonNode.EMPTY_STRING

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
    val arr = JsonArray()
    for (i in range) {
        if (i in this)
            arr += this[i]
    }
    return arr
}

/**
 * Creates a JSON string value of the given Kotlin string value. Returns JSON `null` when the argument is `null`.
 */
fun JsonString(value: String?): JsonNode = JsonNode.string(value)

/**
 * Creates a JSON number value of the given Kotlin number value. Returns JSON `null` when the argument is `null`.
 */
fun JsonNumber(value: Number?): JsonNode = JsonNode.number(value)

/**
 * Creates a JSON boolean value of the given Kotlin boolean value. Returns JSON `null` when the argument is `null`.
 */
fun JsonBool(value: Boolean?): JsonNode = JsonNode.bool(value)

/**
 * Creates an empty JSON object: `{}`.
 */
fun JsonObject(): JsonNode = JsonNode.`object`()

/**
 * Creates an empty JSON array: `[]`.
 */
fun JsonArray(): JsonNode = JsonNode.array()

/**
 * Runs given function on this node and returns itself. Useful for constructs like:
 * ```kotlin
 * return arr {
 *     it += 3
 * }
 * ```
 */
inline operator fun JsonNode.invoke(config: (JsonNode) -> Unit): JsonNode {
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
inline fun JsonObject(config: (JsonNode) -> Unit): JsonNode {
    return JsonObject().invoke(config)
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
inline fun JsonArray(config: (JsonNode) -> Unit): JsonNode {
    return JsonArray().invoke(config)
}

/**
 * Creates a JSON array containing the given elements.
 * @param nodes The elements in the array. Any null values are replaced by JSON null values.
 * @return The newly created array.
 */
fun JsonArray(vararg nodes: JsonNode?): JsonNode = JsonNode.array(*nodes)

/**
 * Creates a JSON array containing the given elements.
 * @param nodes The elements in the array.
 * @return The newly created array.
 */
fun JsonArray(vararg nodes: Int): JsonNode = JsonNode.numberArray(nodes)

/**
 * Creates a JSON array containing the given elements.
 * @param nodes The elements in the array.
 * @return The newly created array.
 */
fun JsonArray(vararg nodes: Byte): JsonNode = JsonNode.numberArray(nodes)

/**
 * Creates a JSON array containing the given elements.
 * @param nodes The elements in the array.
 * @return The newly created array.
 */
fun JsonArray(vararg nodes: Short): JsonNode = JsonNode.numberArray(nodes)

/**
 * Creates a JSON array containing the given elements.
 * @param nodes The elements in the array.
 * @return The newly created array.
 */
fun JsonArray(vararg nodes: Long): JsonNode = JsonNode.numberArray(nodes)

/**
 * Creates a JSON array containing the given elements.
 * @param nodes The elements in the array.
 */
fun JsonArray(vararg nodes: Float): JsonNode = JsonNode.numberArray(nodes)

/**
 * Creates a JSON array containing the given elements.
 * @param nodes The elements in the array.
 * @return The newly created array.
 */
fun JsonArray(vararg nodes: Double): JsonNode = JsonNode.numberArray(nodes)

/**
 * Creates a JSON array containing the given elements.
 * @param nodes The elements in the array.
 * @return The newly created array.
 */
fun JsonArray(vararg nodes: Number): JsonNode = JsonNode.numberArray(*nodes)

/**
 * Creates a JSON array containing the given elements.
 * @param nodes The elements in the array. Any null values are replaced by JSON null values.
 * @return The newly created array.
 */
fun JsonArray(vararg nodes: String?): JsonNode = JsonNode.stringArray(*nodes)

/**
 * Creates a JSON array containing the given elements.
 * @param nodes The elements in the array.
 * @return The newly created array.
 */
fun JsonArray(vararg nodes: Boolean): JsonNode = JsonNode.boolArray(nodes)

/**
 * Creates a JSON array containing the given elements.
 * @param nodes The elements in the array. Any null values are replaced by JSON null values.
 * @return The newly created array.
 */
fun JsonArray(vararg nodes: Boolean?): JsonNode = JsonNode.boolArray(*nodes)

/**
 * Creates a JSON array containing the elements in the given iterable.
 * @param nodes The elements in the array. Any null values are replaced by JSON null values.
 * @return The newly created array.
 */
fun JsonArray(nodes: Iterable<JsonNode?>?): JsonNode = when (nodes) {
    null -> JsonNode.NULL
    else -> JsonNode.array(nodes)
}

private class KtArrayCodec<A>(val codec: JsonCodec<A>, val factory: (Int, (Int) -> A) -> Array<A>) : JsonCodec<Array<A>> {
    override fun encode(obj: Array<A>): JsonNode {
        val arr = JsonArray()
        for (x in obj) arr.add(codec.encode(x))
        return arr
    }

    override fun decode(json: JsonNode): Array<A> {
        json.requireArray()

        return factory(json.size()) {
            codec.decode(json[it])
        }
    }
}

private class KtSequenceCodec<A>(val codec: JsonCodec<A>) : JsonCodec<Sequence<A>> {
    override fun encode(obj: Sequence<A>): JsonNode {
        val arr = JsonArray()
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
fun <A> JsonCodec<A>.arrayOf(factory: (Int, (Int) -> A) -> Array<A>): JsonCodec<Array<A>> {
    return KtArrayCodec(this, factory)
}

/**
 * Creates a [JsonCodec] that encodes and decodes [Array]s of this codec.
 * @return The created codec.
 */
inline fun <reified A> JsonCodec<A>.arrayOf(): JsonCodec<Array<A>> {
    return arrayOf { size, decode -> Array(size) { i -> decode(i) } }
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
infix fun <A> A.encode(codec: JsonCodec<A>): JsonNode = codec.encode(this)

/**
 * Decodes this [JsonNode] to an object using the given codec.
 * @return The decoded object.
 */
infix fun <A> JsonNode.decode(codec: JsonCodec<A>): A = codec.decode(this)

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
inline fun JsonNode.wrap(config: (JsonNode) -> Unit): JsonNode {
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
inline fun JsonNode.wrap(key: String, config: (JsonNode) -> Unit): JsonNode {
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
inline fun JsonNode.copy(config: (JsonNode) -> Unit): JsonNode {
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
inline fun JsonNode.deepCopy(config: (JsonNode) -> Unit): JsonNode {
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
 * The size of this string, object or array node.
 * @throws IncorrectTypeException When the given node is not a string, object or array node.
 */
val JsonNode.indices: IntRange get() = 0..<size

/**
 * Returns an [UByte] value of this node.
 * @return The [UByte] value of this node
 * @throws IncorrectTypeException When this node is not a number node
 */
fun JsonNode.asUByte(): UByte {
    val t = asNumber()
    if (t is UByteWrapper)
        return t.wrap
    if (t is UnparsedNumber)
        return t.unsignedLongValue().toUByte()
    if (t is UnparsedHexNumber)
        return t.toLong().toUByte()

    if (t is Double) {
        if (t < 0.0) return 0u
        if (t > 255.0) return 255u
        return t.toUInt().toUByte()
    }

    if (t is Float) {
        if (t < 0.0f) return 0u
        if (t > 255.0f) return 255u
        return t.toUInt().toUByte()
    }

    return asBigDecimal().toLong().toUByte()
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
    if (t is UnparsedNumber)
        return t.unsignedLongValue().toUShort()
    if (t is UnparsedHexNumber)
        return t.toLong().toUShort()

    if (t is Double) {
        if (t < 0.0) return 0u
        if (t > 65535.0) return 65535u
        return t.toUInt().toUShort()
    }

    if (t is Float) {
        if (t < 0.0f) return 0u
        if (t > 65535.0f) return 65535u
        return t.toUInt().toUShort()
    }

    return asBigDecimal().toLong().toUShort()
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
    if (t is UnparsedNumber)
        return t.unsignedLongValue().toUInt()
    if (t is UnparsedHexNumber)
        return t.toLong().toUInt()

    if (t is Double)
        return t.toUInt()
    if (t is Float)
        return t.toUInt()
    return asBigDecimal().toLong().toUInt()
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
    if (t is UnparsedNumber)
        return t.unsignedLongValue().toULong()
    if (t is UnparsedHexNumber)
        return t.toLong().toULong()

    if (t is Double)
        return t.toULong()
    if (t is Float)
        return t.toULong()
    return asBigDecimal().toLong().toULong()
}


/**
 * Creates a JSON number value of the given Kotlin number value. Returns JSON `null` when the argument is `null`.
 */
fun JsonNumber(value: UByte?): JsonNode = when (value) {
    null -> JsonNode.NULL
    else -> JsonNode.number(UByteWrapper(value))
}

/**
 * Creates a JSON number value of the given Kotlin number value. Returns JSON `null` when the argument is `null`.
 */
fun JsonNumber(value: UShort?): JsonNode = when (value) {
    null -> JsonNode.NULL
    else -> JsonNode.number(UShortWrapper(value))
}

/**
 * Creates a JSON number value of the given Kotlin number value. Returns JSON `null` when the argument is `null`.
 */
fun JsonNumber(value: UInt?): JsonNode = when (value) {
    null -> JsonNode.NULL
    else -> JsonNode.number(UIntWrapper(value))
}

/**
 * Creates a JSON number value of the given Kotlin number value. Returns JSON `null` when the argument is `null`.
 */
fun JsonNumber(value: ULong?): JsonNode = when (value) {
    null -> JsonNode.NULL
    else -> JsonNode.number(ULongWrapper(value))
}


/**
 * Adds a new element to the end of this array. A null value is converted to a JSON null.
 * @param value The new value
 * @return This instance for chaining
 * @throws IncorrectTypeException   If this node is not an array
 */
fun JsonNode.add(value: UByte?): JsonNode = add(JsonNumber(value))

/**
 * Adds a new element to the end of this array. A null value is converted to a JSON null.
 * @param value The new value
 * @return This instance for chaining
 * @throws IncorrectTypeException   If this node is not an array
 */
fun JsonNode.add(value: UShort?): JsonNode = add(JsonNumber(value))

/**
 * Adds a new element to the end of this array. A null value is converted to a JSON null.
 * @param value The new value
 * @return This instance for chaining
 * @throws IncorrectTypeException   If this node is not an array
 */
fun JsonNode.add(value: UInt?): JsonNode = add(JsonNumber(value))

/**
 * Adds a new element to the end of this array. A null value is converted to a JSON null.
 * @param value The new value
 * @return This instance for chaining
 * @throws IncorrectTypeException   If this node is not an array
 */
fun JsonNode.add(value: ULong?): JsonNode = add(JsonNumber(value))

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
operator fun JsonNode.set(index: Int, value: UByte?): JsonNode = set(index, JsonNumber(value))

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
operator fun JsonNode.set(index: Int, value: UShort?): JsonNode = set(index, JsonNumber(value))

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
operator fun JsonNode.set(index: Int, value: UInt?): JsonNode = set(index, JsonNumber(value))

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
operator fun JsonNode.set(index: Int, value: ULong?): JsonNode = set(index, JsonNumber(value))

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
operator fun JsonNode.set(key: String, value: UByte?): JsonNode = set(key, JsonNumber(value))

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
operator fun JsonNode.set(key: String, value: UShort?): JsonNode = set(key, JsonNumber(value))

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
operator fun JsonNode.set(key: String, value: UInt?): JsonNode = set(key, JsonNumber(value))

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
operator fun JsonNode.set(key: String, value: ULong?): JsonNode = set(key, JsonNumber(value))


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

object JsonCodecs {
    val JSON_NODE: JsonCodec<JsonNode> = JsonCodec.JSON_NODE
    val BYTE: JsonCodec<Byte> = JsonCodec.BYTE
    val SHORT: JsonCodec<Short> = JsonCodec.SHORT
    val INT: JsonCodec<Int> = JsonCodec.INT
    val LONG: JsonCodec<Long> = JsonCodec.LONG
    val FLOAT: JsonCodec<Float> = JsonCodec.FLOAT
    val DOUBLE: JsonCodec<Double> = JsonCodec.DOUBLE
    val BIG_INTEGER: JsonCodec<BigInteger> = JsonCodec.BIG_INTEGER
    val BIG_DECIMAL: JsonCodec<BigDecimal> = JsonCodec.BIG_DECIMAL
    val BOOLEAN: JsonCodec<Boolean> = JsonCodec.BOOLEAN
    val STRING: JsonCodec<String> = JsonCodec.STRING
    val EXACT_STRING: JsonCodec<String> = JsonCodec.EXACT_STRING
    val CHAR: JsonCodec<Char> = JsonCodec.CHAR
    val CODE_POINT: JsonCodec<Int> = JsonCodec.CODE_POINT
    val UUID: JsonCodec<UUID> = JsonCodec.UUID
    val INSTANT: JsonCodec<Instant> = JsonCodec.INSTANT
    val LOCAL_DATE: JsonCodec<LocalDate> = JsonCodec.LOCAL_DATE
    val LOCAL_DATE_TIME: JsonCodec<LocalDateTime> = JsonCodec.LOCAL_DATE_TIME
    val LOCAL_TIME: JsonCodec<LocalTime> = JsonCodec.LOCAL_TIME
    val OFFSET_DATE_TIME: JsonCodec<OffsetDateTime> = JsonCodec.OFFSET_DATE_TIME
    val OFFSET_TIME: JsonCodec<OffsetTime> = JsonCodec.OFFSET_TIME
    val YEAR: JsonCodec<Year> = JsonCodec.YEAR
    val MONTH: JsonCodec<Month> = JsonCodec.MONTH
    val YEAR_MONTH: JsonCodec<YearMonth> = JsonCodec.YEAR_MONTH
    val MONTH_DAY: JsonCodec<MonthDay> = JsonCodec.MONTH_DAY
    val ZONED_DATE_TIME: JsonCodec<ZonedDateTime> = JsonCodec.ZONED_DATE_TIME

    /**
     * A [JsonCodec] that encodes and decodes [UByte]s.
     */
    val UBYTE: JsonCodec<UByte> = JsonCodec.of({ JsonNumber(it) }, JsonNode::asUByte)

    /**
     * A [JsonCodec] that encodes and decodes [UShort]s.
     */
    val USHORT: JsonCodec<UShort> = JsonCodec.of({ JsonNumber(it) }, JsonNode::asUShort)

    /**
     * A [JsonCodec] that encodes and decodes [UInt]s.
     */
    val UINT: JsonCodec<UInt> = JsonCodec.of({ JsonNumber(it) }, JsonNode::asUInt)

    /**
     * A [JsonCodec] that encodes and decodes [ULong]s.
     */
    val ULONG: JsonCodec<ULong> = JsonCodec.of({ JsonNumber(it) }, JsonNode::asULong)


    /**
     * A [JsonCodec] that encodes and decodes [IntRange]s as objects `{"from": ..., "to": ...}` where `to` is inclusive.
     */
    val INT_RANGE: JsonCodec<IntRange> = INT.closedRangeOf { x, y -> x..y }

    /**
     * A [JsonCodec] that encodes and decodes [LongRange]s as objects `{"from": ..., "to": ...}` where `to` is inclusive.
     */
    val LONG_RANGE: JsonCodec<LongRange> = LONG.closedRangeOf { x, y -> x..y }

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
    val INT_RANGE_OPEN: JsonCodec<IntRange> = INT.openEndRangeOf { x, y -> x..<y }

    /**
     * A [JsonCodec] that encodes and decodes [LongRange]s as objects `{"from": ..., "to": ...}` where `to` is exclusive.
     */
    val LONG_RANGE_OPEN: JsonCodec<LongRange> = LONG.openEndRangeOf { x, y -> x..<y }

    /**
     * A [JsonCodec] that encodes and decodes [UIntRange]s as objects `{"from": ..., "to": ...}` where `to` is exclusive.
     */
    val UINT_RANGE_OPEN: JsonCodec<UIntRange> = UINT.openEndRangeOf { x, y -> x..<y }

    /**
     * A [JsonCodec] that encodes and decodes [ULongRange]s as objects `{"from": ..., "to": ...}` where `to` is exclusive.
     */
    val ULONG_RANGE_OPEN: JsonCodec<ULongRange> = ULONG.openEndRangeOf { x, y -> x..<y }
}

val String.Companion.codec: JsonCodec<String> get() = JsonCodec.STRING
val Byte.Companion.codec: JsonCodec<Byte> get() = JsonCodec.BYTE
val Short.Companion.codec: JsonCodec<Short> get() = JsonCodec.SHORT
val Int.Companion.codec: JsonCodec<Int> get() = JsonCodec.INT
val Long.Companion.codec: JsonCodec<Long> get() = JsonCodec.LONG

fun <T> JsonCodec<T>.nullableOf(): JsonCodec<T?> {
    return JsonCodec.of<T?>(
        { if (it == null) JsonNull else encode(it) },
        { if (it.isNull) null else decode(it) }
    )
}

/**
 * Creates a codec that encodes [ClosedRange]s of the elements en-/decoded by this codec, in the format
 * `{"from": ..., "to": ...}`.
 */
fun <T : Comparable<T>, R : ClosedRange<T>> JsonCodec<T>.closedRangeOf(factory: (T, T) -> R): JsonCodec<R> {
    return JsonCodec.of({ r: ClosedRange<T> ->
        JsonObject {
            it["from"] = r.start encode this
            it["to"] = r.endInclusive encode this
        }
    }, {
        it.requireObject().requireHas("from").requireHas("to")
        factory(it["from"] decode this, it["to"] decode this)
    })
}

/**
 * Creates a codec that encodes [OpenEndRange]s of the elements en-/decoded by this codec, in the format
 * `{"from": ..., "to": ...}`.
 */
fun <T : Comparable<T>, R : OpenEndRange<T>> JsonCodec<T>.openEndRangeOf(factory: (T, T) -> R): JsonCodec<R> {
    return JsonCodec.of({ r: OpenEndRange<T> ->
        JsonObject {
            it["from"] = r.start encode this
            it["to"] = r.endExclusive encode this
        }
    }, {
        it.requireObject().requireHas("from").requireHas("to")
        factory(it["from"] decode this, it["to"] decode this)
    })
}


// Wrappers for unsigned number types. These wrappers represent the original values as Numbers, which are then
// correctly handled by the library

private data class UByteWrapper(val wrap: UByte) : KotlinUnsignedIntWrapper() {
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

private data class UShortWrapper(val wrap: UShort) : KotlinUnsignedIntWrapper() {
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

private data class UIntWrapper(val wrap: UInt) : KotlinUnsignedIntWrapper() {
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

private data class ULongWrapper(val wrap: ULong) : KotlinUnsignedIntWrapper() {
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
