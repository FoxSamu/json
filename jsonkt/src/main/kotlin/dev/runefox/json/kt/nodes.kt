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

@file:Suppress("unused", "FunctionName")

package dev.runefox.json.kt

import dev.runefox.json.IncorrectSizeException
import dev.runefox.json.IncorrectTypeException
import dev.runefox.json.JsonNode
import dev.runefox.json.NodeType
import dev.runefox.json.codec.JsonCodec
import dev.runefox.json.impl.KotlinUnsignedIntWrapper
import dev.runefox.json.impl.UnparsedHexNumber
import dev.runefox.json.impl.UnparsedNumber
import java.math.BigDecimal
import java.math.BigInteger

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
@ExperimentalKotlinJsonNodeApi
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
@Deprecated("use builtin `.apply { ... }` extension method", ReplaceWith("apply(config)"))
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
    return requireSize(range.first, range.last)
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
    return JsonObject().apply(config)
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
    return JsonArray().apply(config)
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

/**
 * Adds an element to a list.
 * @param value The element to add. Null values will be inserted as JSON null values.
 * @throws IncorrectTypeException When this node is not an array node.
 */
@ExperimentalKotlinJsonNodeApi
operator fun JsonNode.plusAssign(value: Boolean?) {
    add(value)
}

/**
 * Adds an element to a list.
 * @param value The element to add. Null values will be inserted as JSON null values.
 * @throws IncorrectTypeException When this node is not an array node.
 */
@ExperimentalKotlinJsonNodeApi
operator fun JsonNode.plusAssign(value: Number?) {
    add(value)
}

/**
 * Adds an element to a list.
 * @param value The element to add. Null values will be inserted as JSON null values.
 * @throws IncorrectTypeException When this node is not an array node.
 */
@ExperimentalKotlinJsonNodeApi
operator fun JsonNode.plusAssign(value: String?) {
    add(value)
}

/**
 * Adds an element to a list.
 * @param value The element to add. Null values will be inserted as JSON null values.
 * @throws IncorrectTypeException When this node is not an array node.
 */
@ExperimentalKotlinJsonNodeApi
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
@ExperimentalKotlinJsonNodeApi
inline fun JsonNode.wrap(config: (JsonNode) -> Unit): JsonNode {
    return wrap().apply(config)
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
@ExperimentalKotlinJsonNodeApi
inline fun JsonNode.wrap(key: String, config: (JsonNode) -> Unit): JsonNode {
    return wrap(key).apply(config)
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
@ExperimentalKotlinJsonNodeApi
inline fun JsonNode.copy(config: (JsonNode) -> Unit): JsonNode {
    return copy().apply(config)
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
@ExperimentalKotlinJsonNodeApi
inline fun JsonNode.deepCopy(config: (JsonNode) -> Unit): JsonNode {
    return deepCopy().apply(config)
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
