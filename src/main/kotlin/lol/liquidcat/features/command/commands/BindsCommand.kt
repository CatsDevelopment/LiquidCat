/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.command.commands

import lol.liquidcat.LiquidCat
import lol.liquidcat.features.command.Command
import lol.liquidcat.utils.msg
import org.lwjgl.input.Keyboard

class BindsCommand : Command("binds", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            if (args[1].equals("clear", true)) {
                for (module in LiquidCat.moduleManager.modules)
                    module.keyBind = Keyboard.KEY_NONE

                chat("Removed all binds.")
                return
            }
        }

        chat("§c§lBinds")
        LiquidCat.moduleManager.modules.filter { it.keyBind != Keyboard.KEY_NONE }.forEach {
            msg("§6> §c${it.name}: §a§l${Keyboard.getKeyName(it.keyBind)}")
        }
        chatSyntax("binds clear")
    }
}