package fr.o80.gamelib

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UByteVarOf
import kotlinx.cinterop.get
import platform.opengl32.*

fun CPointer<UByteVarOf<GLubyte>>.toKString(): String {
    val nativeBytes = this

    var length = 0
    while (nativeBytes[length].toUByte() != 0.toUByte()) {
        ++length
    }
    val chars = CharArray(length)
    var index = 0
    while (index < length) {
        chars[index] = nativeBytes[index].toInt().toChar()
        ++index
    }
    return chars.concatToString()
}
