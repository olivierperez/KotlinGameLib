package fr.o80.carres.font

data class TTFInfo(
    val tables: Map<String, TTFTable>,
    val head: TTFHead,
    val hhea: TTFHhea,
    val maxp: TTFMaxp,
    val hmtx: TTFHmtx,
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

data class TTFGlyf(
    val numberOfContours: Int16,
    val xMin: Int16,
    val yMin: Int16,
    val xMax: Int16,
    val yMax: Int16,
)

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