package pro.darc.raid.modules.world

import br.com.devsrsouza.kotlinbukkitapi.dsl.command.command
import br.com.devsrsouza.kotlinbukkitapi.extensions.text.msg
import pro.darc.raid.OhMyRaid

fun initialize() {
    OhMyRaid.plugin.command("ohmyraid") {
        aliases = listOf("omr")
        permission = "omr.use"
        permissionMessage = "You don't have permission to use this command"

        executor {
            sender.msg("Usage: /ohmyraid help")
        }

        command("test") {
            executor {
            }
        }

    }
}
