/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.command.commands

import lol.liquidcat.LiquidCat
import lol.liquidcat.features.command.Command
import net.ccbluex.liquidbounce.utils.misc.StringUtils

class ShortcutCommand: lol.liquidcat.features.command.Command("shortcut", arrayOf()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        when {
            args.size > 3 && args[1].equals("add", true) -> {
                try {
                    LiquidCat.commandManager.registerShortcut(args[2],
                            StringUtils.toCompleteString(args, 3))

                    chat("Successfully added shortcut.")
                } catch (e: IllegalArgumentException) {
                    chat(e.message!!)
                }
            }

            args.size >= 3 && args[1].equals("remove", true) -> {
                if (LiquidCat.commandManager.unregisterShortcut(args[2]))
                    chat("Successfully removed shortcut.")
                else
                    chat("Shortcut does not exist.")
            }

            else -> chat("shortcut <add <shortcut_name> <script>/remove <shortcut_name>>")
        }
    }
}
