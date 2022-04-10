/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.file

import com.google.gson.GsonBuilder
import lol.liquidcat.LiquidCat
import lol.liquidcat.file.configs.*
import lol.liquidcat.utils.mc
import java.io.File

object FileManager {

    /**
     * Main client folder
     */
    val mainDir = File(mc.mcDataDir, LiquidCat.CLIENT_NAME).apply {
        if (!exists()) mkdir()
    }

    /**
     * Fonts folder
     */
    val fontsDir = File(mainDir, "fonts").apply {
        if (!exists()) mkdir()
    }

    /**
     * Folder with configs
     */
    val settingsDir = File(mainDir, "settings").apply {
        if (!exists()) mkdir()
    }

    /**
     * Config file containing modules settings
     */
    val modulesConfig = ModulesConfig(File(mainDir, "modules.json"))

    /**
     * Config file containing settings of different values
     */
    val valuesConfig = ValuesConfig(File(mainDir, "values.json"))

    /**
     * Config file containing CilckGUI settings
     */
    val clickGuiConfig = ClickGuiConfig(File(mainDir, "clickgui.json"))

    /**
     * Config file containing saved accounts
     */
    val accountsConfig = AccountsConfig(File(mainDir, "accounts.json"))

    /**
     * Config file containing a list of friends
     */
    val friendsConfig = FriendsConfig(File(mainDir, "friends.json"))

    /**
     * Config file containing a list of blocks allowed for XRay module
     */
    val xrayConfig = XRayConfig(File(mainDir, "xray-blocks.json"))

    /**
     * Config file containing HUD settings
     */
    val hudConfig = HudConfig(File(mainDir, "hud.json"))
    val shortcutsConfig = ShortcutsConfig(File(mainDir, "shortcuts.json"))

    val configs = arrayOf(
        modulesConfig,
        valuesConfig,
        clickGuiConfig,
        accountsConfig,
        friendsConfig,
        xrayConfig,
        hudConfig,
        shortcutsConfig
    )

    val PRETTY_GSON = GsonBuilder().setPrettyPrinting().create()

    /**
     * Load all configs in file manager
     */
    fun loadConfigs() = configs.forEach { loadConfig(it) }

    /**
     * Save all configs in file manager
     */
    fun saveConfigs() = configs.forEach { saveConfig(it) }

    /**
     * Load [config]
     */
    fun loadConfig(config: FileConfig) {
        if (!config.exists()) {
            LiquidCat.logger.info("[FileManager] Skipped loading config: " + config.file.name + ".")
            saveConfig(config, true)
            return
        }
        try {
            config.load()
            LiquidCat.logger.info("[FileManager] Loaded config: " + config.file.name + ".")
        } catch (t: Throwable) {
            LiquidCat.logger.error("[FileManager] Failed to load config file: " + config.file.name + ".", t)
        }
    }

    /**
     * Save [config]
     *
     * @param ignoreStarting check starting
     */
    fun saveConfig(config: FileConfig, ignoreStarting: Boolean = false) {
        if (!ignoreStarting && LiquidCat.loading) return

        try {
            if (!config.exists()) config.create()
            config.save()
            LiquidCat.logger.info("[FileManager] Saved config: " + config.file.name + ".")
        } catch (t: Throwable) {
            LiquidCat.logger.error(
                "[FileManager] Failed to save config file: " +
                        config.file.name + ".", t
            )
        }
    }
}