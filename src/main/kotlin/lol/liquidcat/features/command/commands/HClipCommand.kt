/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.command.commands

import lol.liquidcat.features.command.Command
import lol.liquidcat.utils.entity.forward

class HClipCommand : Command("hclip", emptyArray()) {

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            try {
                mc.thePlayer.forward(args[1].toDouble())
                chat("You were teleported.")
            } catch (exception: NumberFormatException) {
                chatSyntaxError()
            }
            return
        }

        chatSyntax("hclip <value>")
    }
}