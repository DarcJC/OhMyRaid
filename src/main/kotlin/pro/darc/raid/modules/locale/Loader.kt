package pro.darc.raid.modules.locale

import pro.darc.raid.OhMyRaid
import pro.darc.raid.utils.releaseResourceDir
import java.io.File
import java.io.FileNotFoundException

object Loader {

    lateinit var cachedLanguages: List<String>
        private set

    /**
     * Getting the locales list in this plugin
     * Access `cachedLanguages` will be faster when call `getLocaleSupported` once
     */
    @Throws(FileNotFoundException::class)
    fun getLocaleSupported(): List<String> {
        val langFolder = File(OhMyRaid.plugin.dataFolder, "language")
        releaseResourceDir("lang", langFolder)
        val yamlFiles = langFolder.listFiles { _, name -> name.endsWith(".yml") }
        if (yamlFiles == null) {
            OhMyRaid.plugin.logger.severe("Could not load locales")
            throw FileNotFoundException("")
        }
        cachedLanguages = yamlFiles.map {
            it.nameWithoutExtension
        }
        return cachedLanguages
    }
}
