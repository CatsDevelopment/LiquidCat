/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.command.commands

import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager
import lol.liquidcat.utils.ServerUtils
import net.ccbluex.liquidbounce.utils.login.MinecraftAccount

class LoginCommand : lol.liquidcat.features.command.Command("login", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size <= 1) {
            chatSyntax("login <username/email> [password]")
            return
        }

        val result: String = if (args.size > 2)
            GuiAltManager.login(MinecraftAccount(args[1], args[2]))
        else
            GuiAltManager.login(MinecraftAccount(args[1]))

        chat(result)

        if (result.startsWith("§cYour name is now")) {
            if (mc.isIntegratedServerRunning)
                return

            mc.theWorld.sendQuittingDisconnectingPacket()
            ServerUtils.connectToLastServer()
        }
    }
}