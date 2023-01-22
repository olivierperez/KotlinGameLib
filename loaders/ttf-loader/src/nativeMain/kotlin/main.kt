import fr.o80.gamelib.font.TTFReader
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toPath

fun main() = runBlocking {
    try {
        val ttfInfo = TTFReader().read("fonts/Platinum Sign Over.ttf".toPath())
        println(ttfInfo)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
