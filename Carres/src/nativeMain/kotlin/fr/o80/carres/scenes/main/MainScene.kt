package fr.o80.carres.scenes.main

import fr.o80.carres.CarresSceneManager
import fr.o80.carres.font.FontTexture
import fr.o80.carres.font.TextAlign
import fr.o80.carres.image.BmpReader
import fr.o80.carres.image.TextureLoader
import fr.o80.gamelib.Scene
import fr.o80.gamelib.dsl.draw
import fr.o80.gamelib.fontatlas.FileContentProvider
import fr.o80.gamelib.fontatlas.FontAtlas
import fr.o80.gamelib.fontatlas.FontAtlasLoader
import fr.o80.gamelib.loop.KeyPipeline
import fr.o80.gamelib.loop.MouseButtonPipeline
import fr.o80.gamelib.loop.MouseMovePipeline
import fr.o80.gamelib.loop.ScrollPipeline
import fr.o80.gamelib.loop.Window
import fr.o80.gamelib.model.Color
import fr.o80.gamelib.service.Services
import interop.*
import okio.Path.Companion.toPath

class MainScene(
    private val sceneManager: CarresSceneManager
) : Scene {

    private val fontAtlasLoader = FontAtlasLoader()
    private lateinit var fontAtlas: FontAtlas
    private lateinit var fontTexture: FontTexture

    private lateinit var size: Pair<Int, Int>

    private val titleFontHeight = 50f

    override fun open(
        window: Window,
        services: Services,
        keyPipeline: KeyPipeline,
        mouseButtonPipeline: MouseButtonPipeline,
        mouseMovePipeline: MouseMovePipeline,
        scrollPipeline: ScrollPipeline
    ) {
        size = Pair(window.width, window.height)
        keyPipeline.onKey(GLFW_KEY_ESCAPE, GLFW_PRESS) { sceneManager.quit() }
        keyPipeline.onKey(GLFW_KEY_SPACE, GLFW_PRESS) { sceneManager.openDrawing() }
        keyPipeline.onKey(GLFW_KEY_ENTER, GLFW_PRESS) { sceneManager.openDrawing() }

        try {
            fontAtlas = fontAtlasLoader.load(FileContentProvider("fonts/Elnath.fnt".toPath()))
            val fontImagePath = ("fonts/" + fontAtlas.pages.first().file).toPath()
            val fontImage = BmpReader().read(fontImagePath)
            fontTexture = TextureLoader().loadFontTexture(fontAtlas, fontImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun close() {
        fontTexture.unload()
    }

    override suspend fun update() {
    }

    override suspend fun render() {
        draw {
            clear(.098f)

            pushed {
                translate(size.first / 2f - 500f, size.second / 2f - titleFontHeight / 2, 0f)
                fontTexture.render(
                    "LE JEU DES CARRES",
                    maxWidth = 1000f,
                    maxHeight = titleFontHeight,
                    color = Color(a = 255, r = 255, g = 255, b = 255),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
