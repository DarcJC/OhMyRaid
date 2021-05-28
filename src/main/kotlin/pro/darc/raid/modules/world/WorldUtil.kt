package pro.darc.raid.modules.world

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import java.io.*

/**
 * Copy world files
 * @param src from
 * @param dest to
 */
@Throws(IOException::class)
private fun copyWorldFiles(src: File, dest: File) {
    val ignore = listOf("uid.dat", "session.lock")
    if ( ignore.contains(src.name) or dest.exists() ) {
        return
    }

    if (src.isDirectory) {
        if (!dest.mkdirs()) throw IOException("Could not create new world directory!")

        val files = src.list() ?: throw IOException("Could not read world directory files list")
        for (file in files) {
            val from = File(src, file)
            val to = File(dest, file)
            copyWorldFiles(from, to)
        }
    } else if (src.isFile) {
        val ins = FileInputStream(src)
        val ops = FileOutputStream(dest)
        val buffer = ByteArray(1024)
        var length: Int;
        while ((ins.read(buffer).also { length = it }) > 0) {
            ops.write(buffer, 0, length)
        }
        ins.close()
        ops.close()
    }
}

/**
 * Copy a world
 * @param originWorld
 * @param newName
 */
public fun copyWorld(originWorld: World, newName: String): World? {
    copyWorldFiles(originWorld.worldFolder, File(Bukkit.getWorldContainer(), newName))
    return WorldCreator(newName).createWorld()
}

public fun unloadWorld(world: World?): Boolean {
    if (world == null) return false
    return Bukkit.getServer().unloadWorld(world, true)
}

public fun deleteWorld(world: World?): Boolean {
    if (world == null) return false

    val delete = world.worldFolder
    return if (delete.exists()) {
        delete.deleteRecursively()
    } else {
        false
    }
}
