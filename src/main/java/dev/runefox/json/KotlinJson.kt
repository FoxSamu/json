@file:Suppress("unused")

package dev.runefox.json

import dev.runefox.json.codec.JsonCodec

val JSON_NULL: JsonNode = JsonNode.NULL
val JSON_ZERO: JsonNode = JsonNode.ZERO
val JSON_FALSE: JsonNode = JsonNode.FALSE
val JSON_TRUE: JsonNode = JsonNode.TRUE
val JSON_EMPTY_STRING: JsonNode = JsonNode.EMPTY_STRING

fun String.json(): JsonNode = jsonString(this)
fun Number.json(): JsonNode = jsonNumber(this)
fun Boolean.json(): JsonNode = jsonBool(this)

infix fun JsonNode.isType(type: JsonType): Boolean = this.`is`(type)
fun JsonNode.isType(vararg type: JsonType): Boolean = this.`is`(*type)

operator fun JsonNode.contains(key: String): Boolean {
    return this.has(key)
}

fun jsonString(value: String?): JsonNode = JsonNode.string(value)
fun jsonNumber(value: Number?): JsonNode = JsonNode.number(value)
fun jsonBool(value: Boolean?): JsonNode = JsonNode.bool(value)

fun jsonObject(): JsonNode = JsonNode.`object`()
fun jsonArray(): JsonNode = JsonNode.array()

fun jsonArray(vararg nodes: JsonNode?): JsonNode = JsonNode.array(*nodes)
fun jsonArray(vararg nodes: Int): JsonNode = JsonNode.numberArray(nodes)
fun jsonArray(vararg nodes: Byte): JsonNode = JsonNode.numberArray(nodes)
fun jsonArray(vararg nodes: Short): JsonNode = JsonNode.numberArray(nodes)
fun jsonArray(vararg nodes: Long): JsonNode = JsonNode.numberArray(nodes)
fun jsonArray(vararg nodes: Float): JsonNode = JsonNode.numberArray(nodes)
fun jsonArray(vararg nodes: Double): JsonNode = JsonNode.numberArray(nodes)
fun jsonArray(vararg nodes: Number): JsonNode = JsonNode.numberArray(*nodes)
fun jsonArray(vararg nodes: String?): JsonNode = JsonNode.stringArray(*nodes)
fun jsonArray(vararg nodes: Boolean): JsonNode = JsonNode.boolArray(nodes)
fun jsonArray(vararg nodes: Boolean?): JsonNode = JsonNode.boolArray(*nodes)

fun jsonArray(nodes: Iterable<JsonNode>?): JsonNode = when (nodes) {
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

fun <A> JsonCodec<A>.arrayOf(factory: (Int) -> Array<A>): JsonCodec<Array<A>> {
    return KtArrayCodec(this, factory)
}

infix fun <A> A.encoded(codec: JsonCodec<A>): JsonNode = codec.encode(this)
infix fun <A> JsonNode.decoded(codec: JsonCodec<A>): A = codec.decode(this)

operator fun JsonNode.plusAssign(value: Boolean?) {
    add(value)
}

operator fun JsonNode.plusAssign(value: Number?) {
    add(value)
}

operator fun JsonNode.plusAssign(value: String?) {
    add(value)
}

operator fun JsonNode.plusAssign(value: JsonNode?) {
    add(value)
}
