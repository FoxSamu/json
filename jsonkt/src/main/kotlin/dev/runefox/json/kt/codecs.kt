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

@file:OptIn(ExperimentalKotlinJsonNodeApi::class)
@file:Suppress("unused")

package dev.runefox.json.kt

import dev.runefox.json.JsonNode
import dev.runefox.json.codec.JsonCodec
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


@ExperimentalKotlinJsonCodecApi
object JsonCodecs {
    val JsonNode: JsonCodec<JsonNode> = JsonCodec.JSON_NODE
    val Byte: JsonCodec<Byte> = JsonCodec.BYTE
    val Short: JsonCodec<Short> = JsonCodec.SHORT
    val Int: JsonCodec<Int> = JsonCodec.INT
    val Long: JsonCodec<Long> = JsonCodec.LONG
    val Float: JsonCodec<Float> = JsonCodec.FLOAT
    val Double: JsonCodec<Double> = JsonCodec.DOUBLE
    val BigInteger: JsonCodec<BigInteger> = JsonCodec.BIG_INTEGER
    val BigDecimal: JsonCodec<BigDecimal> = JsonCodec.BIG_DECIMAL
    val Boolean: JsonCodec<Boolean> = JsonCodec.BOOLEAN
    val String: JsonCodec<String> = JsonCodec.STRING
    val ExactString: JsonCodec<String> = JsonCodec.EXACT_STRING
    val Char: JsonCodec<Char> = JsonCodec.CHAR
    val CodePoint: JsonCodec<Int> = JsonCodec.CODE_POINT
    val UUID: JsonCodec<UUID> = JsonCodec.UUID
    val Instant: JsonCodec<Instant> = JsonCodec.INSTANT
    val LocalDate: JsonCodec<LocalDate> = JsonCodec.LOCAL_DATE
    val LocalDateTime: JsonCodec<LocalDateTime> = JsonCodec.LOCAL_DATE_TIME
    val LocalTime: JsonCodec<LocalTime> = JsonCodec.LOCAL_TIME
    val OffsetDateTime: JsonCodec<OffsetDateTime> = JsonCodec.OFFSET_DATE_TIME
    val OffsetTime: JsonCodec<OffsetTime> = JsonCodec.OFFSET_TIME
    val Year: JsonCodec<Year> = JsonCodec.YEAR
    val Month: JsonCodec<Month> = JsonCodec.MONTH
    val YearMonth: JsonCodec<YearMonth> = JsonCodec.YEAR_MONTH
    val MonthDay: JsonCodec<MonthDay> = JsonCodec.MONTH_DAY
    val ZonedDateTime: JsonCodec<ZonedDateTime> = JsonCodec.ZONED_DATE_TIME

    /**
     * A [JsonCodec] that encodes and decodes [UByte]s.
     */
    val UByte: JsonCodec<UByte> = JsonCodec.of({ JsonNumber(it) }, { it.asUByte() })

    /**
     * A [JsonCodec] that encodes and decodes [UShort]s.
     */
    val UShort: JsonCodec<UShort> = JsonCodec.of({ JsonNumber(it) }, { it.asUShort() })

    /**
     * A [JsonCodec] that encodes and decodes [UInt]s.
     */
    val UInt: JsonCodec<UInt> = JsonCodec.of({ JsonNumber(it) }, { it.asUInt() })

    /**
     * A [JsonCodec] that encodes and decodes [ULong]s.
     */
    val ULong: JsonCodec<ULong> = JsonCodec.of({ JsonNumber(it) }, { it.asULong() })


    /**
     * A [JsonCodec] that encodes and decodes [IntRange]s as objects `{"from": ..., "to": ...}` where `to` is inclusive.
     */
    val IntRange: JsonCodec<IntRange> = Int.closedRangeOf { x, y -> x..y }

    /**
     * A [JsonCodec] that encodes and decodes [LongRange]s as objects `{"from": ..., "to": ...}` where `to` is inclusive.
     */
    val LongRange: JsonCodec<LongRange> = Long.closedRangeOf { x, y -> x..y }

    /**
     * A [JsonCodec] that encodes and decodes [UIntRange]s as objects `{"from": ..., "to": ...}` where `to` is inclusive.
     */
    val UIntRange: JsonCodec<UIntRange> = UInt.closedRangeOf { x, y -> x..y }

    /**
     * A [JsonCodec] that encodes and decodes [ULongRange]s as objects `{"from": ..., "to": ...}` where `to` is inclusive.
     */
    val ULongRange: JsonCodec<ULongRange> = ULong.closedRangeOf { x, y -> x..y }

    /**
     * A [JsonCodec] that encodes and decodes [IntRange]s as objects `{"from": ..., "to": ...}` where `to` is exclusive.
     */
    val OpenIntRange: JsonCodec<IntRange> = Int.openEndRangeOf { x, y -> x..<y }

    /**
     * A [JsonCodec] that encodes and decodes [LongRange]s as objects `{"from": ..., "to": ...}` where `to` is exclusive.
     */
    val OpenLongRange: JsonCodec<LongRange> = Long.openEndRangeOf { x, y -> x..<y }

    /**
     * A [JsonCodec] that encodes and decodes [UIntRange]s as objects `{"from": ..., "to": ...}` where `to` is exclusive.
     */
    val OpenUIntRange: JsonCodec<UIntRange> = UInt.openEndRangeOf { x, y -> x..<y }

    /**
     * A [JsonCodec] that encodes and decodes [ULongRange]s as objects `{"from": ..., "to": ...}` where `to` is exclusive.
     */
    val OpenULongRange: JsonCodec<ULongRange> = ULong.openEndRangeOf { x, y -> x..<y }
}

@ExperimentalKotlinJsonCodecApi
val String.Companion.codec get() = JsonCodecs.String

@ExperimentalKotlinJsonCodecApi
val Byte.Companion.codec get() = JsonCodecs.Byte

@ExperimentalKotlinJsonCodecApi
val Short.Companion.codec get() = JsonCodecs.Short

@ExperimentalKotlinJsonCodecApi
val Int.Companion.codec get() = JsonCodecs.Int

@ExperimentalKotlinJsonCodecApi
val Long.Companion.codec get() = JsonCodecs.Long

@ExperimentalKotlinJsonCodecApi
val Float.Companion.codec get() = JsonCodecs.Float

@ExperimentalKotlinJsonCodecApi
val Double.Companion.codec get() = JsonCodecs.Double

@ExperimentalKotlinJsonCodecApi
val Char.Companion.codec get() = JsonCodecs.Char

@ExperimentalKotlinJsonCodecApi
val UByte.Companion.codec get() = JsonCodecs.UByte

@ExperimentalKotlinJsonCodecApi
val UShort.Companion.codec get() = JsonCodecs.UShort

@ExperimentalKotlinJsonCodecApi
val UInt.Companion.codec get() = JsonCodecs.UInt

@ExperimentalKotlinJsonCodecApi
val ULong.Companion.codec get() = JsonCodecs.ULong

@ExperimentalKotlinJsonCodecApi
val Boolean.Companion.codec get() = JsonCodecs.Boolean

@ExperimentalKotlinJsonCodecApi
val IntRange.Companion.codec get() = JsonCodecs.IntRange

@ExperimentalKotlinJsonCodecApi
val IntRange.Companion.openCodec get() = JsonCodecs.OpenIntRange

@ExperimentalKotlinJsonCodecApi
val LongRange.Companion.codec get() = JsonCodecs.LongRange

@ExperimentalKotlinJsonCodecApi
val LongRange.Companion.openCodec get() = JsonCodecs.OpenLongRange

@ExperimentalKotlinJsonCodecApi
val UIntRange.Companion.codec get() = JsonCodecs.UIntRange

@ExperimentalKotlinJsonCodecApi
val UIntRange.Companion.openCodec get() = JsonCodecs.OpenUIntRange

@ExperimentalKotlinJsonCodecApi
val ULongRange.Companion.codec get() = JsonCodecs.ULongRange

@ExperimentalKotlinJsonCodecApi
val ULongRange.Companion.openCodec get() = JsonCodecs.OpenULongRange


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

@ExperimentalKotlinJsonCodecApi
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
@ExperimentalKotlinJsonCodecApi
inline fun <T : Comparable<T>, R : ClosedRange<T>> JsonCodec<T>.closedRangeOf(crossinline factory: (T, T) -> R): JsonCodec<R> {
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
@ExperimentalKotlinJsonCodecApi
inline fun <T : Comparable<T>, R : OpenEndRange<T>> JsonCodec<T>.openEndRangeOf(crossinline factory: (T, T) -> R): JsonCodec<R> {
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
