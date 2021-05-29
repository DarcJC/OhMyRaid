package pro.darc.raid.modules.locale

import br.com.devsrsouza.kotlinbukkitapi.architecture.KotlinPlugin
import org.bukkit.configuration.file.YamlConfiguration
import pro.darc.raid.OhMyRaid
import pro.darc.raid.utils.releaseResourceDir
import java.io.File
import java.io.FileNotFoundException

object Loader {

    const val LANGUAGE_DIR = "lang"

    private var cachedLanguages: List<String> = listOf()
    private val localeYAMLs = HashMap<String, YamlConfiguration>()

    /**
     * Getting the locales list in this plugin
     * Access `cachedLanguages` will be faster when call `getLocaleSupported` once
     */
    @Throws(FileNotFoundException::class)
    fun getLocaleSupported(plugin: KotlinPlugin): List<String> {
        val langFolder = File(plugin.dataFolder, LANGUAGE_DIR)
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

    private fun loadAllLocalesYaml(plugin: KotlinPlugin): Map<String, YamlConfiguration> {
        val locales = getLocaleSupported(plugin)
        val langFolder = File(plugin.dataFolder, LANGUAGE_DIR)
        localeYAMLs.clear()
        return locales.associateWith {
            val lf = File(langFolder, "$it.yml")
            localeYAMLs[it] = YamlConfiguration.loadConfiguration(lf)
            localeYAMLs[it]!!
        }
    }

    /**
     * Get locale's YAML file from language string item
     * @param lang Language item, e.g. zh-Hans
     */
    fun OhMyRaid.getLocale(lang: String): YamlConfiguration {
        if (localeYAMLs.isEmpty()) loadAllLocalesYaml(this)
        return localeYAMLs[lang] ?: localeYAMLs[mainConfig.config.default_locale]!!
    }

    /**
     * Check if the locale file exist
     * Must invoke getLocaleSupported once before invoke this method
     *
     * <h2>Example:</h2>
     * <code>
     *     isLocaleSupport("zh-Hans")
     * </code>
     * @param locale local to check
     */
    fun isLocaleSupported(locale: String): Boolean {
        return cachedLanguages.contains(locale)
    }
}


