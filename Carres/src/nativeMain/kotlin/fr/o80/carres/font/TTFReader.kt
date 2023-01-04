package fr.o80.carres.font

import okio.BufferedSource
import okio.ByteString.Companion.toByteString
import okio.FileSystem
import okio.Path
import okio.use

// https://tchayen.github.io/posts/ttf-file-parsing
// https://fontdrop.info/#/?darkmode=true
class TTFReader {
    fun read(path: Path): TTFInfo {
        /*FileSystem.SYSTEM.read(path) {
            val peeked = peek()
            peek().use { it.readByteArray(1) }
            peeked.use {
                it.skip(296)
                it.readByteArray(4).debug("version")
                it.readByteArray(2).debug("numGlyphs")
                it.readByteArray(2).debug("maxPoints")
                exitProcess(0)
            }
        }*/
        return FileSystem.SYSTEM.read(path) {
            var numTables: UInt
            val tables: Map<String, TTFTable> = peek().use { source ->
                source.readUint32("scalarType")
                numTables = source.readUint16("numTables")
                source.readUint16("searchRange")
                source.readUint16("entrySelector")
                source.readUint16("rangeShift")

                source.readTables(numTables)
            }
            println("tables: $tables")

            fun <T : Any> BufferedSource.withTable(tableName: String, block: BufferedSource.() -> T): T {
                return peek().use { source ->
                    val table = tables[tableName] ?: error("\"$tableName\" table not set")
                    source.skip(table.offset.toLong())
                    source.block()
                }.also {
                    println("$tableName: $it")
                }
            }

            val maxp = withTable("maxp") { readMaxp() }
            val head = withTable("head") { readHead() }
            val hhea = withTable("hhea") { readHhea() }
            val hmtx = withTable("hmtx") { readHmtx(hhea.numOfLongHorMetrics, maxp.numGlyphs) }
            val loca = withTable("loca") { readLoca(maxp.numGlyphs) }
            val glyf = withTable("glyf") { readGlyf(loca, if (head.indexToLocFormat == 0) 2 else 1) }

            println("")
            println("")
            println("")
            println("")

            TTFInfo(
                tables,
                head,
                hhea,
                maxp,
                hmtx,
                loca,
                glyf
            )
        }
    }

    private fun BufferedSource.readLoca(numGlyphs: Uint16): List<Int32> {
        return (0U until numGlyphs + 1U).map { readInt32() }
    }

    private fun BufferedSource.readTables(numTables: UInt): Map<String, TTFTable> {
        println("Reading $numTables tables...")
        return (0U until numTables)
            .associate {
                val tag = readString(4)
                val table = TTFTable(
                    checksum = readUint32(),
                    offset = readUint32(),
                    length = readUint32(),
                )

                Pair(tag, table)
            }
    }

    private fun BufferedSource.readHead(): TTFHead {
        println("Reading head...")
        return TTFHead(
            majorVersion = readUint16(),
            minorVersion = readUint16(),
            fontRevision = readFixed(),
            checksumAdjustment = readUint32(),
            magicNumber = readUint32(),
            flags = readUint16(),
            unitsPerEm = readUint16(),
            created = readDate(),
            modified = readDate(),
            xMin = readFWord(),
            yMin = readFWord(),
            xMax = readFWord(),
            yMax = readFWord(),
            macStyle = readUint16(),
            lowestRecPPEM = readUint16(),
            fontDirectionHint = readInt16(),
            indexToLocFormat = readInt16(),
            glyphDataFormat = readInt16()
        )
    }

    private fun BufferedSource.readMaxp(): TTFMaxp {
        println("Reading Maxp")
        return TTFMaxp(
            version = readFixed(),
            numGlyphs = readUint16(),
            maxPoints = readUint16(),
            maxContours = readUint16(),
            maxCompositePoints = readUint16(),
            maxCompositeContours = readUint16(),
            maxZones = readUint16(),
            maxTwilightPoints = readUint16(),
            maxStorage = readUint16(),
            maxFunctionDefs = readUint16(),
            maxInstructionDefs = readUint16(),
            maxStackElements = readUint16(),
            maxSizeOfInstructions = readUint16(),
            maxComponentElements = readUint16(),
            maxComponentDepth = readUint16(),
        )
    }

    private fun BufferedSource.readHhea(): TTFHhea {
        println("Reading HHea...")
        val version = readFixed()
        val ascent = readFWord()
        val descent = readFWord()
        val lineGap = readFWord()
        val advanceWidthMax = readUFWord()
        val minLeftSideBearing = readFWord()
        val minRightSideBearing = readFWord()
        val xMaxExtent = readFWord()
        val caretSlopeRise = readInt16()
        val caretSlopeRun = readInt16()
        val caretOffset = readFWord()

        repeat(4) { readInt16() }

        return TTFHhea(
            version = version,
            ascent = ascent,
            descent = descent,
            lineGap = lineGap,
            advanceWidthMax = advanceWidthMax,
            minLeftSideBearing = minLeftSideBearing,
            minRightSideBearing = minRightSideBearing,
            xMaxExtent = xMaxExtent,
            caretSlopeRise = caretSlopeRise,
            caretSlopeRun = caretSlopeRun,
            caretOffset = caretOffset,
            metricDataFormat = readInt16(),
            numOfLongHorMetrics = readUint16()
        )
    }

    private fun BufferedSource.readHmtx(numOfLongHorMetrics: Uint16, numGlyphs: Uint16): TTFHmtx {
        println("Reading Hmtx...")
        val hMetrics = (0U until numOfLongHorMetrics).map {
            TTFHMetrics(advanceWidth = readUint16(), leftSideBearing = readInt16())
        }

        val leftSideBearing = (0U until numGlyphs - numOfLongHorMetrics).map { readFWord() }

        return TTFHmtx(hMetrics, leftSideBearing)
    }

    private fun BufferedSource.readGlyf(loca: List<Int32>, multiplier: Int): List<TTFGlyf> {
        return loca.map { aLoca ->
            val locaOffset = (aLoca * multiplier).toLong()
            //with(peek()) {
            //skip(locaOffset)
            TTFGlyf(
                0, // readInt16(),
                0, // readInt16(),
                0, // readInt16(),
                0, // readInt16(),
                0, // readInt16(),
            )//.also { this.close() }
            // }
        }
    }
}

private fun BufferedSource.readInt8(debug: String? = null): Int =
    readByteArray(1).debug(debug).toBigEndian()

private fun BufferedSource.readUint8(debug: String? = null): UInt =
    readByteArray(1).udebug(debug).toUBigEndian()

private fun BufferedSource.readInt16(debug: String? = null): Int =
    readByteArray(2).debug(debug).toBigEndian()

private fun BufferedSource.readUint16(debug: String? = null): UInt =
    readByteArray(2).udebug(debug).toUBigEndian()

private fun BufferedSource.readInt32(debug: String? = null): Int =
    readByteArray(4).debug(debug).toBigEndian()

private fun BufferedSource.readUint32(debug: String? = null): UInt =
    readByteArray(4).udebug(debug).toUBigEndian()

private fun BufferedSource.readFixed(debug: String? = null): Fixed = readInt32(debug) / (1 shl 16)

private fun BufferedSource.readUFWord(debug: String? = null): UFWord = readUint16(debug)
private fun BufferedSource.readFWord(debug: String? = null): FWord = readInt16(debug)

private fun BufferedSource.readString(length: Int, debug: String? = null): String {
    val result = StringBuilder()
    repeat(length) {
        val code = readUint8()
        result.append(Char(code.toInt()))
    }
    return result.toString()
}

private fun BufferedSource.readDate(): Date {
    val macTime = readUint32() * 0x100000000U + readUint32()
    return macTime * 1000U + 2082801600000U // (UTC(01/01/1904))
}

private fun ByteArray.toUBigEndian(): UInt {
    return fold(0U) { acc, byte -> acc * 256U + byte.toUByte().toUInt() }
}

private fun ByteArray.toBigEndian(): Int {
    return fold(0) { acc, byte -> acc * 256 + byte }
}

private fun ByteArray.debug(name: String?): ByteArray {
    name?.let {
        println("$name: ${this.joinToString("")} => ${this.toByteString()} => ${this.toBigEndian()}")
    }
    return this
}

private fun ByteArray.udebug(name: String?): ByteArray {
    name?.let {
        println("$name: ${this.joinToString("")} => ${this.toByteString()} => ${this.toUBigEndian()}")
    }
    return this
}
