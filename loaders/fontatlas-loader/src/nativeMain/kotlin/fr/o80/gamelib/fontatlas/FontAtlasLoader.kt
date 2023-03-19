package fr.o80.gamelib.fontatlas

import okio.BufferedSource
import okio.buffer
import okio.use

private val infoRegex =
    "info face=\"([^\"]+)\" size=(\\d+) bold=(\\d+) italic=(\\d+) charset=\"([^\"]*)\" unicode=(\\d+) stretchH=(\\d+) smooth=(\\d+) aa=(\\d+) padding=(\\d+),(\\d+),(\\d+),(\\d+) spacing=(-?\\d+),(-?\\d+)".toRegex()
private val commonRegex =
    "common lineHeight=(\\d+) base=(\\d+) scaleW=(\\d+) scaleH=(\\d+) pages=(\\d+) packed=(\\d+)".toRegex()
private val pageRegex =
    "page id=(\\d+) file=\"([^\"]+)\"".toRegex()
private val charRegex =
    "char id=(\\d+) +x=(\\d+) +y=(\\d+) +width=(\\d+) +height=(\\d+) +xoffset=(-?\\d+) +yoffset=(-?\\d+) +xadvance=(-?\\d+) +page=(\\d+) +chnl=(\\d+) *".toRegex()

class FontAtlasLoader {
    fun load(provider: ContentProvider): FontAtlas {
        return provider.provide().buffer().use { source ->
            val info = source.readInfo()
            val common = source.readCommon()
            val pages = source.readPages()
            val chars = source.readChars()

            FontAtlas(info, common, pages, chars).also { println("atlas:$it") }
        }
    }

}

private fun BufferedSource.readInfo(): FontAtlasInfo {
    val line = this.readUtf8Line()
        ?: throw IllegalArgumentException("Cannot read info line from source")
    val matchResult = infoRegex.find(line)
        ?: throw IllegalArgumentException("Info line is not valid: $line")

    return FontAtlasInfo(
        face = matchResult.groupValues[1],
        size = matchResult.groupValues[2].toInt(),
        bold = matchResult.groupValues[3].toInt(),
        italic = matchResult.groupValues[4].toInt(),
        charset = matchResult.groupValues[5].takeUnless { it.isBlank() },
        unicode = matchResult.groupValues[6].toInt(),
        stretchH = matchResult.groupValues[7].toInt(),
        smooth = matchResult.groupValues[8].toInt(),
        aa = matchResult.groupValues[9].toInt(),
        paddingTop = matchResult.groupValues[10].toInt(),
        paddingRight = matchResult.groupValues[11].toInt(),
        paddingBottom = matchResult.groupValues[12].toInt(),
        paddingLeft = matchResult.groupValues[13].toInt(),
        pacingX = matchResult.groupValues[14].toInt(),
        pacingY = matchResult.groupValues[15].toInt(),
    )
}

private fun BufferedSource.readCommon(): FontAtlasCommon {
    val line = this.readUtf8Line()
        ?: throw IllegalArgumentException("Cannot read common line from source")
    val matchResult = commonRegex.find(line)
        ?: throw IllegalArgumentException("Common line is not valid: $line")

    return FontAtlasCommon(
        lineHeight = matchResult.groupValues[1].toInt(),
        base = matchResult.groupValues[2].toInt(),
        scaleW = matchResult.groupValues[3].toInt(),
        scaleH = matchResult.groupValues[4].toInt(),
        pages = matchResult.groupValues[5].toInt(),
        packed = matchResult.groupValues[6].toInt(),
    )
}

private fun BufferedSource.readPages(): List<FontAtlasPage> {
    val pages = mutableListOf<FontAtlasPage>()
    do {
        val line = this.readUtf8Line()
            ?: throw IllegalArgumentException("Cannot read page line from source")
        if (line.startsWith("chars ")) {
            break
        }

        val matchResult = pageRegex.find(line)
            ?: throw IllegalArgumentException("Page line is not valid: $line")

        pages += FontAtlasPage(
            id = matchResult.groupValues[1].toInt(),
            file = matchResult.groupValues[2],
        )
    } while (true)

    return pages
}

private fun BufferedSource.readChars(): Map<Char, FontAtlasChar> {
    val chars = mutableMapOf<Char, FontAtlasChar>()
    while (!this.exhausted()) {
        val line = this.readUtf8Line()
            ?: throw IllegalArgumentException("Cannot read page line from source")
        val matchResult = charRegex.find(line)
            ?: throw IllegalArgumentException("Char line is not valid: $line")

        val char = matchResult.groupValues[1].toInt().toChar()
        chars[char] = FontAtlasChar(
            char = char,
            x = matchResult.groupValues[2].toInt(),
            y = matchResult.groupValues[3].toInt(),
            width = matchResult.groupValues[4].toInt(),
            height = matchResult.groupValues[5].toInt(),
            xOffset = matchResult.groupValues[6].toInt(),
            yOffset = matchResult.groupValues[7].toInt(),
            xAdvance = matchResult.groupValues[8].toInt(),
            page = matchResult.groupValues[9].toInt(),
            channel = matchResult.groupValues[10].toInt(),
        )
    }

    return chars
}
