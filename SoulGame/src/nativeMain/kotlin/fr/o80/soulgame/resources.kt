package fr.o80.soulgame

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

fun resourceFile(filename: String, fileSystem: FileSystem = FileSystem.SYSTEM): Path {
    filename.toPath()
        .takeIf { fileSystem.exists(it)}
        ?.let { return it }

    val resourceFileName = "SoulGameImpl/resources/$filename"
    resourceFileName.toPath()
        .takeIf { fileSystem.exists(it)}
        ?.let { return it }

    throw IllegalArgumentException("Cannot find file \"$filename\"\n\t- at ./$filename \n\t- at $resourceFileName")
}
