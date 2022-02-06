/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.command.commands

import lol.liquidcat.features.command.Command
import net.ccbluex.liquidbounce.utils.misc.StringUtils

class SayCommand : lol.liquidcat.features.command.Command("say", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            mc.thePlayer.sendChatMessage(StringUtils.toCompleteString(args, 1))
            chat("Message was sent to the chat.")
            return
        }
        chatSyntax("say <message...>")
    }
}