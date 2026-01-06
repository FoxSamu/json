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

package dev.runefox.json

import dev.runefox.json.JsonCodecs.INT_RANGE
import dev.runefox.json.JsonCodecs.INT_RANGE_OPEN
import dev.runefox.json.codec.JsonCodec.INT
import dev.runefox.json.codec.JsonCodec.LOCAL_DATE_TIME
import java.time.LocalDateTime

fun main() {
    val a = JsonObject()

    a["x"] = 3 encode INT
    a["y"] = LocalDateTime.now() encode LOCAL_DATE_TIME

    val b = JsonArray()

    b += "3"
    b += ULong.MAX_VALUE
    b += 3..18 encode INT_RANGE
    b += 3..18 encode INT_RANGE_OPEN

    println(a)
    println(b)
    println(ULong.MAX_VALUE)
    println(a["y"] decode LOCAL_DATE_TIME)
    println(b[2] decode INT_RANGE)
    println(b[3] decode INT_RANGE_OPEN)

    println(JsonNumber(3) == JsonNumber(3))

    val json = Json.json()
    val node = json.parse("{\"x\": ${ULong.MAX_VALUE}}")
    println(node["x"].asULong())
    println(b[1..3])
    println(b in NodeType.ARRAY)
}
