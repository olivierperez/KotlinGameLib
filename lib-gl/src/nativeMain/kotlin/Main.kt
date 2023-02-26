
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.cValuesOf
import kotlinx.cinterop.cstr
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.objcPtr
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.opengl32.*
import platform.windows.byteVar

fun main() {
    try {
        println("-----------------")
        println("- Module OpenGL -")
        println("-----------------")
        val source = """
            |#version 330 core
            |out vec4 FragColor;  
            |in vec3 ourColor;
            |  
            |void main()
            |{
            |    FragColor = vec4(ourColor, 1.0);
            |}
        """.trimMargin()

//        glfwSetKeyCallback()
//        if (glCreateShader != null) {

        /*if (glCreateShader != null) {
            println("not null")
        } else {
            println("null")
        }*/

        /*val shaderId = glCreateShader?.invoke(GL_VERTEX_SHADER.toUInt())
        shaderId?.let {
            memScoped {
                val vShaderCode = cValuesOf(source.cstr.getPointer(memScope))

//                glShaderSource?.invoke(shaderId, 1, vShaderCode.ptr, null)
//                glCompileShader?.invoke(shaderId)

//                val success = alloc<IntVar>()
//                glGetShaderiv?.invoke(shaderId, GL_COMPILE_STATUS.toUInt(), success.ptr)
//                if (success.value == GL_TRUE) {
//                    println("Something gone wrong!")
//                    val infoLog = memScope.allocArray<ByteVar>(512)
//                    glGetShaderInfoLog?.invoke(shaderId, 512, null, infoLog)
//                    return
//                }
            }
        }*/
//        println("glCreateShader: $glCreateShader")
//        println("glUseProgram: $glUseProgram")
    } catch (e: Exception) {
        println("Something failed")
    }

}