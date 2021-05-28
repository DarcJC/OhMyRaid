package pro.darc.raid.utils

import pro.darc.raid.OhMyRaid
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.stream.Stream
import kotlin.io.path.isDirectory

@Throws(FileNotFoundException::class)
fun getResourceDirFiles(path: String, depth: Int = 1): Stream<String> {
    val uri = OhMyRaid::class.java.classLoader.getResource(path)?.toURI()
    val p: Path = if (uri?.scheme.equals("jar")) {
        val fs = FileSystems.newFileSystem(uri, mapOf<String, kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType.Object>())
        fs.getPath(path)
    } else {
        Path.of(uri)
    }
    return Files.walk(p, depth).filter {
        !it.isDirectory() && it.toString() != p.toString()
    }.map {
        it.toString()
    }
}

@Throws(IOException::class)
fun releaseResourceDir(path: String, dest: File) {
    dest.mkdirs()
    if (!dest.isDirectory) throw IOException("Destination ${dest.path} doesn't exist or is not a directory")
    val files = getResourceDirFiles(path)
    for (i in files) {
        val dst = File(dest, i.substringAfter(path))
        val data = OhMyRaid::class.java.classLoader.getResourceAsStream(i.removePrefix("/"))
        if (data == null) {
            OhMyRaid.plugin.logger.warning("Could not open I/O Stream for $i")
            continue
        }
        Files.copy(data, dst.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }
}

@Throws(FileNotFoundException::class)
fun getFileFromResource(path: String): File {
    val url = OhMyRaid::class.java.classLoader.getResource(path) ?: throw FileNotFoundException("Resource file $path not found")
    return File(url.toURI())
}
