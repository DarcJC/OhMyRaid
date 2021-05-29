package pro.darc.raid.modules.locale

import br.com.devsrsouza.kotlinbukkitapi.dsl.command.arguments.string
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.command
import br.com.devsrsouza.kotlinbukkitapi.extensions.event.KListener
import br.com.devsrsouza.kotlinbukkitapi.extensions.event.event
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import pro.darc.raid.OhMyRaid
import pro.darc.raid.modules.locale.Loader.getLocale
import pro.darc.raid.modules.locale.Loader.getLocaleSupported
import pro.darc.raid.utils.getModuleDataFolder
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object UUIDAsStringSerializer: KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: UUID) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): UUID = UUID.fromString(decoder.decodeString())
}

@Serializable
data class PlayerLocale(
    @Serializable(with =  UUIDAsStringSerializer::class)
    val uuid: UUID,
    var language: String,
) {

    companion object {
        private val playerLocaleDataMap = ConcurrentHashMap<UUID, PlayerLocale>()

        fun fromUUID(uuid: UUID): PlayerLocale {
            val targetFile = File(OhMyRaid.plugin.getModuleDataFolder("locale"), "$uuid.json")
            if (targetFile.exists())
                return Json.decodeFromString(targetFile.readText())
            return PlayerLocale(uuid, OhMyRaid.plugin.mainConfig.config.default_locale)
        }
        fun getPlayerLocaleOrDefault(uuid: UUID): YamlConfiguration {
             return playerLocaleDataMap[uuid]?.let {
                 OhMyRaid.plugin.getLocale(it.language)
             } ?: OhMyRaid.plugin.getLocale("")
        }
        class LocaleListeners(override val plugin: OhMyRaid): KListener<OhMyRaid> {
            init {
                event<PlayerJoinEvent>(priority = EventPriority.MONITOR) {
                    // Priority set to monitor to create player locale data ASAP
                    if (playerLocaleDataMap.containsKey(player.uniqueId)) return@event
                    val pl = fromUUID(player.uniqueId)
                    playerLocaleDataMap[player.uniqueId] = pl
                }

                event<PlayerQuitEvent> {
                    playerLocaleDataMap[player.uniqueId]?.save()
                }

                command("olang") {
                    aliases = listOf("olanguage", "突袭语言")
                    permission = "orm.use"
                    permissionMessage = "You don't have permission to use this command"

                    command("list") {
                        executor {
                            sender.sendString("language-list", mapOf(Pair("list", getLocaleSupported(this@LocaleListeners.plugin).toString())))
                        }
                    }

                    executorPlayer {
                        val targetLang = string(0)
                        if (!Loader.isLocaleSupported(targetLang)) {
                            sender.sendString("language-not-supported")
                            return@executorPlayer
                        }
                        playerLocaleDataMap[this.sender.uniqueId]?.language = targetLang
                        sender.sendString("language-set", mapOf(Pair("lang", targetLang)))
                    }
                }
            }
        }
    }

    fun save() {
        val targetFile = File(OhMyRaid.plugin.getModuleDataFolder("locale"), "$uuid.json")
        targetFile.writeText(Json.encodeToString(this))
    }
}

fun Player.getLocaleYAML(): YamlConfiguration = PlayerLocale.getPlayerLocaleOrDefault(this.uniqueId)
