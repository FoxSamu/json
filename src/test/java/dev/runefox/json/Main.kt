package dev.runefox.json

import dev.runefox.json.codec.JsonCodec
import java.time.LocalDateTime

fun main() {
    var a = jsonObject()

    a["x"] = 3 encoded JsonCodec.INT
    a["y"] = LocalDateTime.now() encoded JsonCodec.LOCAL_DATE_TIME

    var b = jsonArray()

    b += "3"
    b += 9

    println(a)
    println(b)
    println(a["y"] decoded JsonCodec.LOCAL_DATE_TIME)
}
