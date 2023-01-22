package fr.o80.gamelib.font

data class TTFInfo(
    val tables: Map<String, TTFTable>,
    val head: TTFHead,
    val hhea: TTFHhea,
    val maxp: TTFMaxp,
    val hmtx: TTFHmtx,
    val cmap: TTFCmap,
    val local: List<Int32>,
    val glyf: List<TTFGlyf>
) {
    override fun toString(): String {
        return "TTFInfo(\ntables=$tables\nhead=$head\nhhea=$hhea\nmaxp=$maxp\nhmtx=$hmtx\nlocal=$local\nglyf=$glyf)"
    }
}

data class TTFTable(
    val checksum: UInt,
    val offset: UInt,
    val length: UInt,
)

data class TTFHead(
    val majorVersion: Uint16,
    val minorVersion: Uint16,
    val fontRevision: Fixed,
    val checksumAdjustment: Uint32,
    val magicNumber: Uint32,
    val flags: Uint16,
    val unitsPerEm: Uint16,
    val created: Date,
    val modified: Date,
    val xMin: FWord,
    val yMin: FWord,
    val xMax: FWord,
    val yMax: FWord,
    val macStyle: Uint16,
    val lowestRecPPEM: Uint16,
    val fontDirectionHint: Int16,
    val indexToLocFormat: Int16,
    val glyphDataFormat: Int16,
)

data class TTFMaxp(
    val version: Fixed,
    val numGlyphs: Uint16,
    val maxPoints: Uint16,
    val maxContours: Uint16,
    val maxCompositePoints: Uint16,
    val maxCompositeContours: Uint16,
    val maxZones: Uint16,
    val maxTwilightPoints: Uint16,
    val maxStorage: Uint16,
    val maxFunctionDefs: Uint16,
    val maxInstructionDefs: Uint16,
    val maxStackElements: Uint16,
    val maxSizeOfInstructions: Uint16,
    val maxComponentElements: Uint16,
    val maxComponentDepth: Uint16,
)

data class TTFHhea(
    val version: Fixed,
    val ascent: FWord,
    val descent: FWord,
    val lineGap: FWord,
    val advanceWidthMax: UFWord,
    val minLeftSideBearing: FWord,
    val minRightSideBearing: FWord,
    val xMaxExtent: FWord,
    val caretSlopeRise: Int16,
    val caretSlopeRun: Int16,
    val caretOffset: FWord,
    val metricDataFormat: Int16,
    val numOfLongHorMetrics: Uint16
)

data class TTFHmtx(
    val hMetrics: List<TTFHMetrics>,
    val leftSideBearing: List<FWord>
)

data class TTFHMetrics(
    val advanceWidth: Uint16,
    val leftSideBearing: Int16
)

data class TTFCmap(
    val version: Int,
    val numTables: Int,
    val encodings: List<Encoding>,
    val subTable: SubTable,
) {
    data class Encoding(
        val platformId: Int,
        val encodingId: Int,
        val offset: UInt,
    )
    data class SubTable(
        val format: UInt,
        val length: UInt,
        val language: UInt,
        val segCount: UInt,
        val searchRange: UInt,
        val entrySelector: UInt,
        val rangeShift: UInt,
        val segments: List<Segment>,
        val glyphIdArray: List<UInt>
    )
    data class Segment(
        val startCode: UInt,
        val endCode: UInt,
        val idDelta: Int,
        val idRangeOffsets: UInt
    )
}

data class TTFGlyf(
    val numberOfContours: Int16,
    val xMin: Int16,
    val yMin: Int16,
    val xMax: Int16,
    val yMax: Int16,
    val endPtsOfContours: List<UInt>,
    val instructions: List<UInt>,
    val coordinates: List<Pair<Int, Int>>,
)

data class TTFGlyfFlag(
    val onCurvePoint: Boolean,
    val xReadShort: Boolean,
    val yReadShort: Boolean,
    val repeat: Boolean,
    val xShortIsPositive: Boolean,
    val yShortIsPositive: Boolean,
    val xIsSame: Boolean,
    val yIsSame: Boolean,
    val overlapSimple: Boolean
) {
    companion object {
        fun fromInt(value: UInt): TTFGlyfFlag {
            val xReadShort = value and 0x02U == 0x02U
            val yReadShort = value and 0x04U == 0x04U
            val xIsSameOrPositiveXShortVector = value and 0x10U == 0x10U
            val yIsSameOrPositiveYShortVector = value and 0x20U == 0x20U
            return TTFGlyfFlag(
                onCurvePoint = value and 0x01U == 0x01U,
                xReadShort = xReadShort,
                yReadShort = yReadShort,
                repeat = value and 0x08U == 0x08U,
                xIsSame = !xReadShort && xIsSameOrPositiveXShortVector,
                yIsSame = !yReadShort && yIsSameOrPositiveYShortVector,
                overlapSimple = value and 0x40U == 0x40U,
                xShortIsPositive = xReadShort && xIsSameOrPositiveXShortVector,
                yShortIsPositive = yReadShort && yIsSameOrPositiveYShortVector
            )
        }
    }
}

typealias Date = ULong
typealias Fixed = Int
typealias FWord = Int
typealias UFWord = UInt
typealias Uint8 = UInt
typealias Int8 = Int
typealias Uint16 = UInt
typealias Int16 = Int
typealias Uint32 = UInt
typealias Int32 = Int