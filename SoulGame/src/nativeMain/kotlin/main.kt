import fr.o80.soulgame.SoulGame
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    try {
        println("Hello, ${gameLibName()}!")
        SoulGame().start()
        println("End of ${gameLibName()}.")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
