/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.command.commands

import lol.liquidcat.LiquidCat
import lol.liquidcat.features.command.Command
import lol.liquidcat.features.module.ModuleManager
import lol.liquidcat.file.FileManager
import net.ccbluex.liquidbounce.script.ScriptManager
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui
import net.ccbluex.liquidbounce.ui.font.Fonts

object ReloadCommand : Command("reload", arrayOf("configreload")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        chat("Reloading...")
        LiquidCat.loading = true
        ScriptManager.disableScripts()
        ScriptManager.unloadScripts()

        for (module in ModuleManager.modules)
            ModuleManager.generateCommand(module)

        chat("§c§lReloading scripts...")
        ScriptManager.loadScripts()
        ScriptManager.enableScripts()
        chat("§c§lReloading fonts...")
        Fonts.loadFonts()
        chat("§c§lReloading modules...")
        FileManager.loadConfig(FileManager.modulesConfig)
        LiquidCat.loading = false
        chat("§c§lReloading values...")
        FileManager.loadConfig(FileManager.valuesConfig)
        chat("§c§lReloading accounts...")
        FileManager.loadConfig(FileManager.accountsConfig)
        chat("§c§lReloading friends...")
        FileManager.loadConfig(FileManager.friendsConfig)
        chat("§c§lReloading xray...")
        FileManager.loadConfig(FileManager.xrayConfig)
        chat("§c§lReloading HUD...")
        FileManager.loadConfig(FileManager.hudConfig)
        chat("§c§lReloading ClickGUI...")
        LiquidCat.clickGui = ClickGui()
        FileManager.loadConfig(FileManager.clickGuiConfig)
        chat("Reloaded.")
    }
}
