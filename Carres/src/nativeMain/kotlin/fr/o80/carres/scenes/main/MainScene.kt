package fr.o80.carres.scenes.main

import fr.o80.carres.CarresSceneManager
import fr.o80.carres.image.BmpReader
import fr.o80.carres.image.FontTexture
import fr.o80.carres.image.Texture
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
import fr.o80.gamelib.service.Services
import interop.*
import okio.Path.Companion.toPath

class MainScene(
    private val sceneManager: CarresSceneManager
) : Scene {

    private val fontAtlasLoader = FontAtlasLoader()
    private lateinit var fontAtlas: FontAtlas
    private lateinit var fontTexture: FontTexture

    private var texture: Texture? = null

    private lateinit var size: Pair<Int, Int>

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
            val imagePath = "images/logo.bmp".toPath()
            val image = BmpReader().read(imagePath)
            texture = TextureLoader().loadTexture(image)

            fontAtlas = fontAtlasLoader.load(FileContentProvider("fonts/Platinum Sign Over.fnt".toPath()))
            val fontImagePath = ("fonts/" + fontAtlas.pages.first().file).toPath()
            val fontImage = BmpReader().read(fontImagePath)
            fontTexture = TextureLoader().loadFontTexture(fontAtlas, fontImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun close() {
    }

    override suspend fun update() {
    }

    override suspend fun render() {
        draw {
            clear(.098f)

            texture?.let {
                pushed {
                    translate(size.first / 2f - it.width / 2f, size.second / 2f - it.height / 2f, 0f)
                    it.render()
                }
            }

            pushed {
                fontTexture.render('A', 50f)
            }
        }
    }
}
