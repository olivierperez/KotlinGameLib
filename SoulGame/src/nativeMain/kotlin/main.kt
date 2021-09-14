import fr.o80.gamelib.image.ImageIO
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toPath

fun main() = runBlocking {
    try {
        println("Hello, ${gameLibName()}!")
//        SoulGame().start()
        val image = ImageIO().read("pointer.png".toPath())
        println("image: $image")
        println("End of ${gameLibName()}.")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
