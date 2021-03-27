package com.edgin.around.game

import java.io.InputStream

class MessageProcessor(val stream: InputStream) {
    val EOM: Char = '\n'
    var bytes = ByteArray(1024)
    var builder = StringBuilder(1024)

    fun process(): Array<String> {
        val length = stream.read(bytes)
        builder.append(String(bytes, 0, length, Charsets.UTF_8))

        val string = builder.toString()
        val end = string.lastIndexOf(EOM)
        builder.delete(0, end + 1)

        return string.split(EOM).dropLast(1).toTypedArray()
    }
}
