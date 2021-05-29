package pro.darc.raid.modules.locale

import net.md_5.bungee.api.ChatColor
import org.apache.commons.lang.text.StrSubstitutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
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
        val sub = StrSubstitutor(args, "\${", "}")
        sub.replace(it).colorize()
    }
}

/**
 * Sending a string message to receivers
 * @param path string path in yaml file
 * @param receiver
 * @param args args to format the message
 */
fun YamlConfiguration.sendString(path: String, receiver: Collection<CommandSender>, args: Map<String, String> = mapOf()): Boolean {
    return getString(path)?.let {
        val msg = it.formatStringNamed(args)
        receiver.forEach { sender ->
            sender.sendMessage(msg)
        }
        true
    } ?: false
}
