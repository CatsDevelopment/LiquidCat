/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.command.commands

import lol.liquidcat.LiquidCat
import lol.liquidcat.features.command.Command
import lol.liquidcat.features.module.ModuleManager
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import org.lwjgl.input.Keyboard

class BindCommand : Command("bind", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 2) {
            // Get module by name
            val module = ModuleManager.getModule(args[1])

            if (module == null) {
                chat("Module §a§l" + args[1] + "§3 not found.")
                return
            }
            // Find key by name and change
            val key = Keyboard.getKeyIndex(args[2].toUpperCase())
            module.keyBind = key

            // Response to user
            chat("Bound module §a§l${module.name}§3 to key §a§l${Keyboard.getKeyName(key)}§3.")
            LiquidCat.hud.addNotification(Notification("Bound ${module.name} to ${Keyboard.getKeyName(key)}"))
            playEdit()
            return
        }

        chatSyntax(arrayOf("<module> <key>", "<module> none"))
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