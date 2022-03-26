/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.command.commands

import lol.liquidcat.LiquidCat
import lol.liquidcat.features.command.Command
import lol.liquidcat.features.command.CommandManager
import lol.liquidcat.features.module.ModuleManager
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui
import net.ccbluex.liquidbounce.ui.font.Fonts

object ReloadCommand : Command("reload", arrayOf("configreload")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        chat("Reloading...")
        LiquidCat.isStarting = true
        LiquidCat.scriptManager.disableScripts()
        LiquidCat.scriptManager.unloadScripts()

        for (module in ModuleManager.modules)
            ModuleManager.generateCommand(module)

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
