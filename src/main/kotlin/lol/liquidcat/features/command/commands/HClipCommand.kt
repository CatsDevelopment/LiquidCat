/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package lol.liquidcat.features.command.commands

import lol.liquidcat.features.command.Command
import net.ccbluex.liquidbounce.utils.MovementUtils

class HClipCommand : lol.liquidcat.features.command.Command("hclip", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            try {
                MovementUtils.forward(args[1].toDouble())
                chat("You were teleported.")
            } catch (exception: NumberFormatException) {
                chatSyntaxError()
            }
            return
        }

        chatSyntax("hclip <value>")
    }
}