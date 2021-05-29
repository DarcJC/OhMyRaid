package pro.darc.raid.modules.locale

import br.com.devsrsouza.kotlinbukkitapi.architecture.KotlinPlugin
import net.md_5.bungee.api.ChatColor
import org.apache.commons.lang.text.StrSubstitutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import pro.darc.raid.OhMyRaid
import pro.darc.raid.modules.locale.Loader.getLocale
import pro.darc.raid.utils.releaseResourceDir
import java.io.File
import java.util.regex.Pattern


private fun String.colorize(
    from: String = "&",
    to: String = "§",
    hexPattern: Pattern = Pattern.compile("(?<!\\\\\\\\)(#[a-fA-F0-9]{6})")
): String {
    var res = this.replace(from, to)
    val matcher = hexPattern.matcher(this)
    while (matcher.find()) {
        val color = this.substring(matcher.start(), matcher.end())
        res = this.replace(color, "${ChatColor.of(color)}")
    }

    return res
}

/**
 * Formatting strings like
 * <code>
 *     "I'm ${name}, age is ${age}"
 * </code>
 *
 * @param path yaml path
 * @param args args map
 */
fun String.formatStringNamed(args: Map<String, String>): String {
    return this.let {
        val sub = StrSubstitutor(args, "#{", "}")
        sub.replace(it).colorize()
    }
}

fun YamlConfiguration.formatString(path: String, args: Map<String, String> = mapOf()): String {
    return getString(path).let {
        it?.formatStringNamed(args) ?: getString("missing-key")!!
    }
}

/**
 * Sending a string message to receivers
 * @param path string path in yaml file
 * @param receiver
 * @param args args to format the message
 */
fun YamlConfiguration.sendString(path: String, receiver: Collection<CommandSender>, args: Map<String, String> = mapOf()) {
    formatString(path, args).let {
        receiver.forEach { sender ->
            sender.sendMessage(it)
        }
    }
}

fun CommandSender.sendString(path: String, args: Map<String, String> = mapOf()) {
    val locale: YamlConfiguration = if (this is Player) this.getLocaleYAML()
    else OhMyRaid.plugin.getLocale("")
    locale.sendString(path, listOf(this), args)
}

fun initialize(plugin: OhMyRaid) {
    val langFolder = File(plugin.dataFolder, Loader.LANGUAGE_DIR)
    releaseResourceDir("lang", langFolder)
    PlayerLocale.Companion.LocaleListeners(plugin)
}
