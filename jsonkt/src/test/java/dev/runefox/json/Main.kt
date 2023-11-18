package dev.runefox.json

import dev.runefox.json.codec.JsonCodec.INT
import dev.runefox.json.codec.JsonCodec.LOCAL_DATE_TIME
import java.time.LocalDateTime

fun main() {
    val a = jsonObject()

    a["x"] = 3 encoded INT
    a["y"] = LocalDateTime.now() encoded LOCAL_DATE_TIME

    val b = jsonArray()

    b += "3"
    b += ULong.MAX_VALUE
    b += 3..18 encoded INT_RANGE
    b += 3..18 encoded INT_RANGE_OPEN

    println(a)
    println(b)
    println(ULong.MAX_VALUE)
    println(a["y"] decoded LOCAL_DATE_TIME)
    println(b[2] decoded INT_RANGE)
    println(b[3] decoded INT_RANGE_OPEN)

    println(jsonNumber(3) == jsonNumber(3))

    val json = Json.json()
    val node = json.parse("{\"x\": ${ULong.MAX_VALUE}}")
    println(node["x"].asULong())
    println(b[1..3])
    println(b in NodeType.ARRAY)
}
