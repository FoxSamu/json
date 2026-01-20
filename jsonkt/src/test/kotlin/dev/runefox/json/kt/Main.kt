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

package dev.runefox.json.kt

import dev.runefox.json.Json
import dev.runefox.json.NodeType
import dev.runefox.json.kt.JsonCodecs.IntRange
import dev.runefox.json.kt.JsonCodecs.OpenIntRange
import dev.runefox.json.codec.JsonCodec.INT
import dev.runefox.json.codec.JsonCodec.LOCAL_DATE_TIME
import java.time.LocalDateTime

@OptIn(ExperimentalKotlinJsonCodecApi::class, ExperimentalKotlinJsonNodeApi::class)
fun main() {
    val a = JsonObject()

    a["x"] = 3 encode INT
    a["y"] = LocalDateTime.now() encode LOCAL_DATE_TIME

    val b = JsonArray()

    b += "3"
    b += ULong.MAX_VALUE
    b += 3..18 encode IntRange
    b += 3..18 encode OpenIntRange

    println(a)
    println(b)
    println(ULong.MAX_VALUE)
    println(a["y"] decode LOCAL_DATE_TIME)
    println(b[2] decode IntRange)
    println(b[3] decode OpenIntRange)

    println(JsonNumber(3) == JsonNumber(3))

    val json = Json.json()
    val node = json.parse("{\"x\": ${ULong.MAX_VALUE}}")
    println(node["x"].asULong())
    println(b[1..3])
    println(b in NodeType.ARRAY)
}
