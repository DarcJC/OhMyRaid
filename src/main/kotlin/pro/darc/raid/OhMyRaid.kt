package pro.darc.raid

import br.com.devsrsouza.kotlinbukkitapi.architecture.KotlinPlugin
import br.com.devsrsouza.kotlinbukkitapi.serialization.architecture.config
import pro.darc.raid.schema.ConfigV1
import pro.darc.raid.utils.mergeYAML
import pro.darc.raid.modules.world.initialize as initializeWorld

class OhMyRaid : KotlinPlugin() {

    companion object {
        lateinit var plugin: OhMyRaid
            private set
    }

    val mainConfig by lazy {
        mergeYAML("config.yml")
        val c = config(
            "config.yml",
            ConfigV1(),
            ConfigV1.serializer(),
            alwaysRestoreDefaults = false,
        )
        c.load()
        c
    }

    override fun onPluginLoad() {
        plugin = this
    }

    override fun onPluginEnable() {
        initializeWorld()
    }

    override fun onPluginDisable() {
    }
}