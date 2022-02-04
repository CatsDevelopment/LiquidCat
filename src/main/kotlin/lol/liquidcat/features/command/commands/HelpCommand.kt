/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package lol.liquidcat.features.command.commands

import joptsimple.internal.Strings
import lol.liquidcat.LiquidCat
import lol.liquidcat.features.command.Command
import net.ccbluex.liquidbounce.utils.ClientUtils

class HelpCommand : lol.liquidcat.features.command.Command("help", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        var page = 1


        if (args.size > 1) {
            try {
                page = args[1].toInt()
            } catch (e: NumberFormatException) {
                chatSyntaxError()
            }
        }

        if (page <= 0) {
            chat("The number you have entered is too low, it must be over 0")
            return
        }

        val maxPageDouble = LiquidCat.commandManager.commands.size / 8.0
        val maxPage = if (maxPageDouble > maxPageDouble.toInt())
            maxPageDouble.toInt() + 1
        else
            maxPageDouble.toInt()

        if (page > maxPage) {
            chat("The number you have entered is too big, it must be under $maxPage.")
            return
        }

        chat("§c§lHelp")
        ClientUtils.displayChatMessage("§7> Page: §8$page / $maxPage")

        val commands = LiquidCat.commandManager.commands.sortedBy { it.command }

        var i = 8 * (page - 1)
        while (i < 8 * page && i < commands.size) {
            val command = commands[i]

            ClientUtils.displayChatMessage("§6> §7${LiquidCat.commandManager.prefix}${command.command}${if (command.alias.isEmpty()) "" else " §7(§8" + Strings.join(command.alias, "§7, §8") + "§7)"}")
            i++
        }

        ClientUtils.displayChatMessage("§a------------\n§7> §c${LiquidCat.commandManager.prefix}help §8<§7§lpage§8>")
    }
}