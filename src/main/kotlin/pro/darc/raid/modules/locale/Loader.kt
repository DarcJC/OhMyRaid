package pro.darc.raid.modules.locale

import br.com.devsrsouza.kotlinbukkitapi.architecture.KotlinPlugin
import org.bukkit.configuration.file.YamlConfiguration
import pro.darc.raid.OhMyRaid
import pro.darc.raid.utils.releaseResourceDir
import java.io.File
import java.io.FileNotFoundException

object Loader {

    const val LANGUAGE_DIR = "lang"

    lateinit var cachedLanguages: List<String>
        private set

    /**
     * Getting the locales list in this plugin
     * Access `cachedLanguages` will be faster when call `getLocaleSupported` once
     */
    @Throws(FileNotFoundException::class)
    fun getLocaleSupported(plugin: KotlinPlugin): List<String> {
        val langFolder = File(plugin.dataFolder, LANGUAGE_DIR)
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

    fun loadAllLocalesYaml(plugin: KotlinPlugin): Map<String, YamlConfiguration> {
        val locales = getLocaleSupported(plugin)
        val langFolder = File(plugin.dataFolder, LANGUAGE_DIR)
        return locales.associateWith {
            val lf = File(langFolder, "$it.yml")
            YamlConfiguration.loadConfiguration(lf)
        }
    }
}


