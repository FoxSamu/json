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

@file:Suppress("FunctionName")

package dev.runefox.json.kt

import dev.runefox.json.IncorrectTypeException
import dev.runefox.json.JsonNode
import dev.runefox.json.impl.KotlinUnsignedIntWrapper
import dev.runefox.json.impl.UnparsedHexNumber
import dev.runefox.json.impl.UnparsedNumber
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Returns an [UByte] value of this node.
 * @return The [UByte] value of this node
 * @throws IncorrectTypeException When this node is not a number node
 */
@ExperimentalKotlinJsonNodeApi
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
@ExperimentalKotlinJsonNodeApi
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
@ExperimentalKotlinJsonNodeApi
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
@ExperimentalKotlinJsonNodeApi
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
@ExperimentalKotlinJsonNodeApi
fun JsonNumber(value: UByte?): JsonNode = when (value) {
    null -> JsonNode.NULL
    else -> JsonNode.number(UByteWrapper(value))
}

/**
 * Creates a JSON number value of the given Kotlin number value. Returns JSON `null` when the argument is `null`.
 */
@ExperimentalKotlinJsonNodeApi
fun JsonNumber(value: UShort?): JsonNode = when (value) {
    null -> JsonNode.NULL
    else -> JsonNode.number(UShortWrapper(value))
}

/**
 * Creates a JSON number value of the given Kotlin number value. Returns JSON `null` when the argument is `null`.
 */
@ExperimentalKotlinJsonNodeApi
fun JsonNumber(value: UInt?): JsonNode = when (value) {
    null -> JsonNode.NULL
    else -> JsonNode.number(UIntWrapper(value))
}

/**
 * Creates a JSON number value of the given Kotlin number value. Returns JSON `null` when the argument is `null`.
 */
@ExperimentalKotlinJsonNodeApi
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
@ExperimentalKotlinJsonNodeApi
fun JsonNode.add(value: UByte?): JsonNode = add(JsonNumber(value))

/**
 * Adds a new element to the end of this array. A null value is converted to a JSON null.
 * @param value The new value
 * @return This instance for chaining
 * @throws IncorrectTypeException   If this node is not an array
 */
@ExperimentalKotlinJsonNodeApi
fun JsonNode.add(value: UShort?): JsonNode = add(JsonNumber(value))

/**
 * Adds a new element to the end of this array. A null value is converted to a JSON null.
 * @param value The new value
 * @return This instance for chaining
 * @throws IncorrectTypeException   If this node is not an array
 */
@ExperimentalKotlinJsonNodeApi
fun JsonNode.add(value: UInt?): JsonNode = add(JsonNumber(value))

/**
 * Adds a new element to the end of this array. A null value is converted to a JSON null.
 * @param value The new value
 * @return This instance for chaining
 * @throws IncorrectTypeException   If this node is not an array
 */
@ExperimentalKotlinJsonNodeApi
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
@ExperimentalKotlinJsonNodeApi
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
@ExperimentalKotlinJsonNodeApi
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
@ExperimentalKotlinJsonNodeApi
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
@ExperimentalKotlinJsonNodeApi
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
@ExperimentalKotlinJsonNodeApi
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
@ExperimentalKotlinJsonNodeApi
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
@ExperimentalKotlinJsonNodeApi
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
@ExperimentalKotlinJsonNodeApi
operator fun JsonNode.set(key: String, value: ULong?): JsonNode = set(key, JsonNumber(value))


/**
 * Adds an element to a list.
 * @param value The element to add. Null values will be inserted as JSON null values.
 * @throws IncorrectTypeException When this node is not an array node.
 */
@ExperimentalKotlinJsonNodeApi
operator fun JsonNode.plusAssign(value: UByte?) {
    add(value)
}


/**
 * Adds an element to a list.
 * @param value The element to add. Null values will be inserted as JSON null values.
 * @throws IncorrectTypeException When this node is not an array node.
 */
@ExperimentalKotlinJsonNodeApi
operator fun JsonNode.plusAssign(value: UShort?) {
    add(value)
}


/**
 * Adds an element to a list.
 * @param value The element to add. Null values will be inserted as JSON null values.
 * @throws IncorrectTypeException When this node is not an array node.
 */
@ExperimentalKotlinJsonNodeApi
operator fun JsonNode.plusAssign(value: UInt?) {
    add(value)
}


/**
 * Adds an element to a list.
 * @param value The element to add. Null values will be inserted as JSON null values.
 * @throws IncorrectTypeException When this node is not an array node.
 */
@ExperimentalKotlinJsonNodeApi
operator fun JsonNode.plusAssign(value: ULong?) {
    add(value)
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
