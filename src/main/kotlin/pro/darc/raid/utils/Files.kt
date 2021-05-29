package pro.darc.raid.utils

import br.com.devsrsouza.kotlinbukkitapi.architecture.KotlinPlugin
import pro.darc.raid.OhMyRaid
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile


@Throws(IOException::class)
fun releaseResourceDir(path: String, dest: File, replace: Boolean = true) {
    val jar = JarFile(File(OhMyRaid::class.java.protectionDomain.codeSource.location.toURI()))
    copyResourcesToDirectory(jar, path, dest, replace)
}

@Throws(FileNotFoundException::class)
fun getFileFromResource(path: String): File {
    val url = OhMyRaid::class.java.classLoader.getResource(path) ?: throw FileNotFoundException("Resource file $path not found")
    return File(url.toURI())
}

fun KotlinPlugin.getModuleDataFolder(module: String): File {
    return File(dataFolder, "data/$module").let {
        if (!it.exists()) it.mkdirs()
        it
    }
}

@Throws(IOException::class)
fun copyResourcesToDirectory(fromJar: JarFile, jarDir: String, destDir: File, replace: Boolean = false) {
    val entries: Enumeration<JarEntry> = fromJar.entries()
    while (entries.hasMoreElements()) {
        val entry: JarEntry = entries.nextElement()
        if (entry.name.startsWith("$jarDir/") && !entry.isDirectory) {
            val dest = File(destDir, entry.name.substring(jarDir.length + 1))
            if (dest.exists() && !replace) continue
            val parent = dest.parentFile
            parent?.mkdirs()
            val out = FileOutputStream(dest)
            val `in` = fromJar.getInputStream(entry)
            try {
                val buffer = ByteArray(8 * 1024)
                var s = 0
                while (`in`.read(buffer).also { s = it } > 0) {
                    out.write(buffer, 0, s)
                }
            } catch (e: IOException) {
                throw IOException("Could not copy asset from jar file", e)
            } finally {
                try {
                    `in`.close()
                } catch (ignored: IOException) {
                }
                try {
                    out.close()
                } catch (ignored: IOException) {
                }
            }
        }
    }
}
