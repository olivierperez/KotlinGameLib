package fr.o80.gamelib.font

import okio.BufferedSource
import okio.ByteString.Companion.toByteString
import okio.FileSystem
import okio.Path
import okio.use

// https://tchayen.github.io/posts/ttf-file-parsing
// https://fontdrop.info/#/?darkmode=true
class TTFReader {
    fun read(path: Path): TTFInfo {
        return FileSystem.SYSTEM.read(path) {
            var numTables: UInt
            val tables: Map<String, TTFTable> = peek().use { source ->
                source.readUint32()
                numTables = source.readUint16()
                source.readUint16()
                source.readUint16()
                source.readUint16()

                source.readTables(numTables)
            }

            fun <T : Any> BufferedSource.withTable(tableName: String, block: BufferedSource.(offset: UInt) -> T): T {
                return peek().use { source ->
                    val table = tables[tableName] ?: error("\"$tableName\" table not set")
                    source.skip(table.offset.toLong())
                    source.block(table.offset)
                }
            }

            val maxp = withTable("maxp") { readMaxp() }
            val head = withTable("head") { readHead() }
            val hhea = withTable("hhea") { readHhea() }
            val hmtx = withTable("hmtx") { readHmtx(hhea.numOfLongHorMetrics, maxp.numGlyphs) }
            val loca = withTable("loca") { readLoca(head.indexToLocFormat, maxp.numGlyphs) }
            val cmap = withTable("cmap") { readCmap() }

            val glyfTable = tables["glyf"] ?: error("\"glyf\" table not set")
            val glyf = readGlyf(glyfTable.offset, loca)

            TTFInfo(
                tables,
                head,
                hhea,
                maxp,
                hmtx,
                cmap,
                loca,
                glyf
            )
        }
    }

    private fun BufferedSource.readLoca(indexToLocFormat: Int16, numGlyphs: Uint16): List<Int32> {
        return if (indexToLocFormat == 0) {
            (0U until numGlyphs).map { readInt16() * 2 }
        } else {
            (0U until numGlyphs).map { readInt32() }
        }
    }

    private fun BufferedSource.readTables(numTables: UInt): Map<String, TTFTable> {
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
        val hMetrics = (0U until numOfLongHorMetrics).map {
            TTFHMetrics(advanceWidth = readUint16(), leftSideBearing = readInt16())
        }

        val leftSideBearing = (0U until numGlyphs - numOfLongHorMetrics).map { readFWord() }

        return TTFHmtx(hMetrics, leftSideBearing)
    }

    private fun BufferedSource.readGlyf(globalOffset: UInt, loca: List<Int32>): List<TTFGlyf> {
        return loca
            .map { aLoca -> globalOffset.toLong() + aLoca.toLong() }
            .mapIndexed { index, offset ->
                peek().use { source ->
                    val debugGlyf = index == -1
                    if (debugGlyf) {
                        println("Glyph $index")
                        println("offset: $offset")
                    }

                    source.skip(offset)
                    val numberOfContours = source.readInt16()
                    val xMin = source.readInt16()
                    val yMin = source.readInt16()
                    val xMax = source.readInt16()
                    val yMax = source.readInt16()

                    val endPtsOfContours = (0 until numberOfContours).map { source.readUint16() }
                    val instructionLength = source.readUint16()
                    val instructions = (0U until instructionLength).map { source.readUint8() }
                    val flags = (0U..endPtsOfContours.last()).map { source.readUint8() }.map(TTFGlyfFlag::fromInt)

                    val xCoordinates = flags
                        .mapNotNull {
                            when {
                                it.xIsSame -> null

                                it.xReadShort -> if (it.xShortIsPositive) {
                                    { source.readUint8().toInt() }
                                } else {
                                    { -(source.readUint8().toInt()) }
                                }

                                else -> {
                                    { source.readInt16() }
                                }
                            }
                        }
                        .map { read -> read() }

                    val yCoordinates = flags
                        .mapNotNull {
                            when {
                                it.yReadShort -> if (it.yShortIsPositive) {
                                    { source.readUint8().toInt() }
                                } else {
                                    { -(source.readUint8().toInt()) }
                                }

                                !it.yIsSame -> {
                                    { source.readInt16() }
                                }

                                else -> null
                            }
                        }
                        .map { it() }

                    var ix = 0
                    var iy = 0
                    val coordinates = flags.runningFold(Pair(0, 0)) { acc, flag ->
                        val relX = if (flag.xIsSame) 0 else xCoordinates[ix++]
                        val relY = if (flag.yIsSame) 0 else yCoordinates[iy++]

                        if (debugGlyf) println("rel: $relX, $relY")
                        Pair(acc.first + relX, acc.second + relY)
                    }

                    if (debugGlyf) {
                        flags.debug("flags")
                        xCoordinates.debug("xCoordinates")
                        yCoordinates.debug("yCoordinates")
                        coordinates.drop(1).debug("coordinates")
                    }

                    TTFGlyf(
                        numberOfContours,
                        xMin,
                        yMin,
                        xMax,
                        yMax,
                        endPtsOfContours,
                        instructions,
                        coordinates.drop(1)
                    ).also {
                        if (debugGlyf) {
                            println("===================================")
                            println("===================================")
                            println("glyf: $it")
                            println("===================================")
                            println("===================================")
                        }
                    }
                }
            }
    }

    private fun BufferedSource.readCmap(): TTFCmap {
        val version = readInt16()
        val numTables = readInt16()

        val encodings = (0 until numTables).map {
            TTFCmap.Encoding(
                platformId = readInt16(),
                encodingId = readInt16(),
                offset = readOffset32()
            )
        }

        val selectedEncoding = encodings.find {
            val isWindowsPlatform = it.platformId == 3 &&
                    (it.encodingId == 0 || it.encodingId == 1 || it.encodingId == 10)

            val isUnicodePlatform = it.platformId == 0 &&
                    (it.encodingId == 0 ||
                            it.encodingId == 1 ||
                            it.encodingId == 2 ||
                            it.encodingId == 3 ||
                            it.encodingId == 4)

            isWindowsPlatform || isUnicodePlatform
        }
        val selectedOffset = selectedEncoding?.offset // TODO Comprendre ce que je dois faire de ça

        val format = readUint16()
        val length = readUint16()
        val language = readUint16()
        val segCount = (readUint16() / 2U)
        val searchRange = readUint16()
        val entrySelector = readUint16()
        val rangeShift = readUint16()

        val endCodes = (0U until segCount).map { readUint16() }
        readUint16() // reservedPad
        val startCodes = (0U until segCount).map { readUint16() }
        val idDeltas = (0U until segCount).map { readInt16() }
        val idRangeOffsets = (0U until segCount).map { readUint16() }

        val glyphIdArray = (0 until 4).map { readUint16() }

        val segments = (0U until segCount).mapIndexed { i, _ ->
            TTFCmap.Segment(
                startCodes[i],
                endCodes[i],
                idDeltas[i],
                idRangeOffsets[i]
            )
        }

        val subTable = TTFCmap.SubTable(
            format = format,
            length = length,
            language = language,
            segCount = segCount,
            searchRange = searchRange,
            entrySelector = entrySelector,
            rangeShift = rangeShift,
            segments = segments,
            glyphIdArray = glyphIdArray
        )

        return TTFCmap(
            version,
            numTables,
            encodings,
            subTable
        )
    }
}

private fun <E> List<E>.debug(name: String): List<E> {
    println("$name:\n${this.joinToString("\n") { "- $it" }}")
    return this
}

private fun BufferedSource.readInt8(debug: String? = null): Int8 =
    readByte().toInt().debug(debug)

private fun BufferedSource.readUint8(debug: String? = null): Uint8 =
    readByte().toUByte().toUInt().debug(debug)

private fun BufferedSource.readInt16(name: String? = null): Int16 {
    val a = readByte().toUByte().toInt()
    val b = readByte().toUByte().toInt()

    val number = (a shl 8) or b

    val result = if (number and 0x8000 == 0x8000) {
        number - (1 shl 16)
    } else {
        number
    }

    return result.debug(name)
}

private fun BufferedSource.readUint16(debug: String? = null): Uint16 {
    val a = readUint8(debug)
    val b = readUint8(debug)
    val c = a shl 8 or b
    return c.debug(debug)
}

private fun BufferedSource.readInt32(debug: String? = null): Int32 =
    readByteArray(4).debug(debug).toBigEndian()

private fun BufferedSource.readUint32(debug: String? = null): Uint32 =
    readByteArray(4).udebug(debug).toUBigEndian()

private fun BufferedSource.readFixed(debug: String? = null): Fixed = readInt32(debug) / (1 shl 16)

private fun BufferedSource.readUFWord(debug: String? = null): UFWord = readUint16(debug)
private fun BufferedSource.readFWord(debug: String? = null): FWord = readInt16(debug)

private fun BufferedSource.readOffset32(debug: String? = null): UInt = readUint32(debug)

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
    return fold(0) { acc, byte -> acc * 256 or byte.toInt() }
}

private fun ByteArray.debug(name: String?): ByteArray {
    name?.let {
        println("$name: ${this.joinToString()} => (hex)${this.joinToString { it.toString(16) }} => ${this.toByteString()} => ${this.toBigEndian()}")
    }
    return this
}

private fun Byte.debug(name: String?): Byte {
    name?.let {
        println("$name: 0x${this.toString(16)} => $this")
    }
    return this
}

private fun Int.debug(name: String?): Int {
    name?.let {
        println("$name: 0x${this.toString(16)} => $this")
    }
    return this
}

private fun UInt.debug(name: String?): UInt {
    name?.let {
        println("$name: 0x${this.toString(16)} => $this")
    }
    return this
}

private fun ByteArray.udebug(name: String?): ByteArray {
    name?.let {
        println("$name: ${this.joinToString("")} => ${this.toByteString()} => ${this.toUBigEndian()}")
    }
    return this
}
