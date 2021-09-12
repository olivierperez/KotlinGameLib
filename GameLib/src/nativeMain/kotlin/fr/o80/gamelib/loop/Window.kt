package fr.o80.gamelib.loop

import cnames.structs.GLFWwindow
import kotlinx.cinterop.CPointer

class Window(
    val id: CPointer<GLFWwindow>?,
    val width: Int,
    val height: Int
)
