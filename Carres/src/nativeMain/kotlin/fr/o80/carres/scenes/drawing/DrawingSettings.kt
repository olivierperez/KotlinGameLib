package fr.o80.carres.scenes.drawing

data class DrawingSettings(
    val gridWidth: Float = 3f,
    val margin: Float = 20f,
    val numberWidth: Float = 3f,
    val numberMargin: Float = .3f,
    val paddingOfColoredCell: Float = .15f,
    val zoomSpeed: Float = .9f
)
