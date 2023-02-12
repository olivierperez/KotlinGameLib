package fr.o80.gamelib.fontatlas

import okio.Buffer
import okio.Source
import kotlin.test.Test
import kotlin.test.assertEquals

class StringContentProvider(
    private val content: String
) : ContentProvider {
    override fun provide(): Source {
        return Buffer().write(content.encodeToByteArray())
    }
}

class FontAtlasLoaderTest {
    private val loader = FontAtlasLoader()
    private val oneCharFontAtlas = """
                |info face="Platinum Sign Over" size=50 bold=0 italic=0 charset="" unicode=0 stretchH=100 smooth=1 aa=1 padding=3,3,3,3 spacing=-2,-2
                |common lineHeight=83 base=66 scaleW=512 scaleH=512 pages=1 packed=0
                |page id=0 file="Platinum Sign Over.png"
                |chars count=2
                |char id=65      x=0    y=0    width=85   height=49   xoffset=-13  yoffset=10   xadvance=49   page=0    chnl=0 
                |char id=66      x=85   y=0    width=81   height=49   xoffset=-13  yoffset=10   xadvance=46   page=0    chnl=0 
                """.trimMargin()

    @Test
    fun shouldLoadInfo() {
        // When
        val fontAtlas = loader.load(StringContentProvider(oneCharFontAtlas))

        // Then
        assertEquals(
            FontAtlasInfo(
                "Platinum Sign Over",
                size = 50,
                bold = 0,
                italic = 0,
                charset = null,
                unicode = 0,
                stretchH = 100,
                smooth = 1,
                aa = 1,
                paddingTop = 3,
                paddingRight = 3,
                paddingBottom = 3,
                paddingLeft = 3,
                pacingX = -2,
                pacingY = -2
            ), fontAtlas.info
        )
    }

    @Test
    fun shouldLoadCommon() {
        // When
        val fontAtlas = loader.load(StringContentProvider(oneCharFontAtlas))

        // Then
        assertEquals(
            FontAtlasCommon(
                lineHeight = 83,
                base = 66,
                scaleW = 512,
                scaleH = 512,
                pages = 1,
                packed = 0,
            ), fontAtlas.common
        )
    }

    @Test
    fun shouldLoadPages() {
        // When
        val fontAtlas = loader.load(StringContentProvider(oneCharFontAtlas))

        // Then
        assertEquals(
            listOf(
                FontAtlasPage(
                    0,
                    "Platinum Sign Over.png"
                )
            ),
            fontAtlas.pages
        )
    }

    @Test
    fun shouldLoadChars() {
        // When
        val fontAtlas = loader.load(StringContentProvider(oneCharFontAtlas))

        // Then
        assertEquals(
            FontAtlasChar(
                char = 'A',
                x = 0,
                y = 0,
                width = 85,
                height = 49,
                xOffset = -13,
                yOffset = 10,
                xAdvance = 49,
                page = 0,
                channel = 0
            ), fontAtlas.getChar('A')
        )
        assertEquals(
            FontAtlasChar(
                char = 'B',
                x = 85,
                y = 0,
                width = 81,
                height = 49,
                xOffset = -13,
                yOffset = 10,
                xAdvance = 46,
                page = 0,
                channel = 0
            ), fontAtlas.getChar('B')
        )
    }
}
