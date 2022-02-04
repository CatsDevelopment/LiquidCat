/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package lol.liquidcat.features.command.commands

import lol.liquidcat.LiquidCat
import lol.liquidcat.features.command.Command
import lol.liquidcat.features.command.CommandManager
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui
import net.ccbluex.liquidbounce.ui.font.Fonts

class ReloadCommand : lol.liquidcat.features.command.Command("reload", arrayOf("configreload")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        chat("Reloading...")
        chat("§c§lReloading commands...")
        LiquidCat.commandManager = CommandManager()
        LiquidCat.commandManager.registerCommands()
        LiquidCat.isStarting = true
        LiquidCat.scriptManager.disableScripts()
        LiquidCat.scriptManager.unloadScripts()
        for(module in LiquidCat.moduleManager.modules)
            LiquidCat.moduleManager.generateCommand(module)
        chat("§c§lReloading scripts...")
        LiquidCat.scriptManager.loadScripts()
        LiquidCat.scriptManager.enableScripts()
        chat("§c§lReloading fonts...")
        Fonts.loadFonts()
        chat("§c§lReloading modules...")
        LiquidCat.fileManager.loadConfig(LiquidCat.fileManager.modulesConfig)
        LiquidCat.isStarting = false
        chat("§c§lReloading values...")
        LiquidCat.fileManager.loadConfig(LiquidCat.fileManager.valuesConfig)
        chat("§c§lReloading accounts...")
        LiquidCat.fileManager.loadConfig(LiquidCat.fileManager.accountsConfig)
        chat("§c§lReloading friends...")
        LiquidCat.fileManager.loadConfig(LiquidCat.fileManager.friendsConfig)
        chat("§c§lReloading xray...")
        LiquidCat.fileManager.loadConfig(LiquidCat.fileManager.xrayConfig)
        chat("§c§lReloading HUD...")
        LiquidCat.fileManager.loadConfig(LiquidCat.fileManager.hudConfig)
        chat("§c§lReloading ClickGUI...")
        LiquidCat.clickGui = ClickGui()
        LiquidCat.fileManager.loadConfig(LiquidCat.fileManager.clickGuiConfig)
        chat("Reloaded.")
    }
}
