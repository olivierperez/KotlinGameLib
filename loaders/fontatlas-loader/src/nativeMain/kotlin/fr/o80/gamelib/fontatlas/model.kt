package fr.o80.gamelib.fontatlas

data class FontAtlas(
    val info: FontAtlasInfo,
    val common: FontAtlasCommon,
    val pages: List<FontAtlasPage>,
    val chars: Map<Char, FontAtlasChar>
) {
    fun getChar(char: Char): FontAtlasChar? {
        return chars[char]
    }
}

data class FontAtlasInfo(
    val face: String,
    val size: Int,
    val bold: Int,
    val italic: Int,
    val charset: String?,
    val unicode: Int,
    val stretchH: Int,
    val smooth: Int,
    val aa: Int,
    val paddingTop: Int,
    val paddingRight: Int,
    val paddingBottom: Int,
    val paddingLeft: Int,
    val pacingX: Int,
    val pacingY: Int
)

data class FontAtlasCommon(
    val lineHeight: Int,
    val base: Int,
    val scaleW: Int,
    val scaleH: Int,
    val pages: Int,
    val packed: Int
)

data class FontAtlasPage(
    val id: Int,
    val file: String
)

data class FontAtlasChar(
    val char: Char,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val xOffset: Int,
    val yOffset: Int,
    val xAdvance: Int,
    val page: Int,
    val channel: Int
)
