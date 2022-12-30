import fr.o80.carres.CarresGame
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    try {
        println("Hello, ${gameLibName()}!")
        CarresGame().start()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
