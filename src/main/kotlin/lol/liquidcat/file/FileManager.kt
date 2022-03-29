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
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileInputStream
import javax.imageio.ImageIO

object FileManager {
    val dir = File(mc.mcDataDir, LiquidCat.CLIENT_NAME)
    val fontsDir = File(dir, "fonts")
    val settingsDir = File(dir, "settings")

    val modulesConfig = ModulesConfig(File(dir, "modules.json"))
    val valuesConfig = ValuesConfig(File(dir, "values.json"))
    val clickGuiConfig = ClickGuiConfig(File(dir, "clickgui.json"))
    val accountsConfig = AccountsConfig(File(dir, "accounts.json"))
    val friendsConfig = FriendsConfig(File(dir, "friends.json"))
    val xrayConfig = XRayConfig(File(dir, "xray-blocks.json"))
    val hudConfig = HudConfig(File(dir, "hud.json"))
    val shortcutsConfig = ShortcutsConfig(File(dir, "shortcuts.json"))

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
     * Setup folder
     */
    fun setupFolder() {
        if (!dir.exists()) dir.mkdir()
        if (!fontsDir.exists()) fontsDir.mkdir()
        if (!settingsDir.exists()) settingsDir.mkdir()
    }

    /**
     * Load one config
     *
     * @param config to load
     */
    fun loadConfig(config: FileConfig) {
        if (!config.hasConfig()) {
            LiquidCat.logger.info("[FileManager] Skipped loading config: " + config.file.name + ".")
            saveConfig(config, true)
            return
        }
        try {
            config.loadConfig()
            LiquidCat.logger.info("[FileManager] Loaded config: " + config.file.name + ".")
        } catch (t: Throwable) {
            LiquidCat.logger.error("[FileManager] Failed to load config file: " + config.file.name + ".", t)
        }
    }

    /**
     * Save one config
     *
     * @param config         to save
     * @param ignoreStarting check starting
     */
    fun saveConfig(config: FileConfig, ignoreStarting: Boolean = false) {
        if (!ignoreStarting && LiquidCat.isStarting) return
        try {
            if (!config.hasConfig()) config.createConfig()
            config.saveConfig()
            LiquidCat.logger.info("[FileManager] Saved config: " + config.file.name + ".")
        } catch (t: Throwable) {
            LiquidCat.logger.error(
                "[FileManager] Failed to save config file: " +
                        config.file.name + ".", t
            )
        }
    }

    /**
     * Constructor of file manager
     * Setup everything important
     */
    init {
        setupFolder()
    }
}