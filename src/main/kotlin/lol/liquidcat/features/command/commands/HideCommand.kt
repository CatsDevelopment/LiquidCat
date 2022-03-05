/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.command.commands

import lol.liquidcat.LiquidCat
import lol.liquidcat.features.command.Command
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.utils.msg
import net.ccbluex.liquidbounce.utils.ClientUtils

class HideCommand : lol.liquidcat.features.command.Command("hide", emptyArray()) {

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            when {
                args[1].equals("list", true) -> {
                    chat("§c§lHidden")
                    LiquidCat.moduleManager.modules.filter { !it.array }.forEach {
                        msg("§6> §c${it.name}")
                    }
                    return
                }

                args[1].equals("clear", true) -> {
                    for (module in LiquidCat.moduleManager.modules)
                        module.array = true

                    chat("Cleared hidden modules.")
                    return
                }

                args[1].equals("reset", true) -> {
                    for (module in LiquidCat.moduleManager.modules)
                        module.array = module::class.java.getAnnotation(ModuleInfo::class.java).array

                    chat("Reset hidden modules.")
                    return
                }

                else -> {
                    // Get module by name
                    val module = LiquidCat.moduleManager.getModule(args[1])

                    if (module == null) {
                        chat("Module §a§l${args[1]}§3 not found.")
                        return
                    }

                    // Find key by name and change
                    module.array = !module.array

                    // Response to user
                    chat("Module §a§l${module.name}§3 is now §a§l${if (module.array) "visible" else "invisible"}§3 on the array list.")
                    playEdit()
                    return
                }
            }
        }

        chatSyntax("hide <module/list/clear/reset>")
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        val moduleName = args[0]

        return when (args.size) {
            1 -> LiquidCat.moduleManager.modules
                    .map { it.name }
                    .filter { it.startsWith(moduleName, true) }
                    .toList()
            else -> emptyList()
        }
    }

}