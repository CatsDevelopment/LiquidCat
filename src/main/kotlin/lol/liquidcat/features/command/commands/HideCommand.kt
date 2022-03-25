/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.command.commands

import lol.liquidcat.LiquidCat
import lol.liquidcat.features.command.Command
import lol.liquidcat.features.module.ModuleManager
import lol.liquidcat.utils.msg

class HideCommand : Command("hide", emptyArray()) {

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            when {
                args[1].equals("list", true) -> {
                    chat("§c§lHidden")
                    ModuleManager.modules.filter { it.hide }.forEach {
                        msg("§6> §c${it.name}")
                    }
                    return
                }

                args[1].equals("clear", true) -> {
                    for (module in ModuleManager.modules)
                        module.hide = false

                    chat("Cleared hidden modules.")
                    return
                }

                else -> {
                    // Get module by name
                    val module = ModuleManager.getModule(args[1])

                    if (module == null) {
                        chat("Module §a§l${args[1]}§3 not found.")
                        return
                    }

                    // Find key by name and change
                    module.hide = !module.hide

                    // Response to user
                    chat("Module §a§l${module.name}§3 is now §a§l${if (module.hide) "invisible" else "visible"}§3 on the array list.")
                    playEdit()
                    return
                }
            }
        }

        chatSyntax("hide <module/list/clear>")
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        val moduleName = args[0]

        return when (args.size) {
            1 -> ModuleManager.modules
                    .map { it.name }
                    .filter { it.startsWith(moduleName, true) }
                    .toList()
            else -> emptyList()
        }
    }
}