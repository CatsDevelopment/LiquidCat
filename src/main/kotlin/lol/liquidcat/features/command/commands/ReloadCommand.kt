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
import lol.liquidcat.file.configs.*
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
        FileManager.loadConfig(ModulesConfig)
        LiquidCat.loading = false
        chat("§c§lReloading values...")
        FileManager.loadConfig(ValuesConfig)
        chat("§c§lReloading accounts...")
        FileManager.loadConfig(AccountsConfig)
        chat("§c§lReloading friends...")
        FileManager.loadConfig(FriendsConfig)
        chat("§c§lReloading xray...")
        FileManager.loadConfig(XRayConfig)
        chat("§c§lReloading HUD...")
        FileManager.loadConfig(HudConfig)
        chat("§c§lReloading ClickGUI...")
        LiquidCat.clickGui = ClickGui()
        FileManager.loadConfig(ClickGuiConfig)
        chat("Reloaded.")
    }
}
