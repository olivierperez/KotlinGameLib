package fr.o80.gamelib.fontatlas

import okio.FileSystem
import okio.Path
import okio.Source

interface ContentProvider {
    fun provide(): Source
}

class FileContentProvider(
    private val path: Path
):ContentProvider {
    override fun provide(): Source = FileSystem.SYSTEM.source(path)
}
