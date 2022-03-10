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

class FileManager {
    val dir: File = File(mc.mcDataDir, LiquidCat.CLIENT_NAME)
    @JvmField
    val fontsDir = File(dir, "fonts")
    val settingsDir = File(dir, "settings")
    val modulesConfig: FileConfig = ModulesConfig(File(dir, "modules.json"))
    @JvmField
    val valuesConfig: FileConfig = ValuesConfig(File(dir, "values.json"))
    @JvmField
    val clickGuiConfig: FileConfig = ClickGuiConfig(File(dir, "clickgui.json"))
    @JvmField
    val accountsConfig: AccountsConfig = AccountsConfig(File(dir, "accounts.json"))
    @JvmField
    val friendsConfig: FriendsConfig = FriendsConfig(File(dir, "friends.json"))
    val xrayConfig: FileConfig = XRayConfig(File(dir, "xray-blocks.json"))
    val hudConfig: FileConfig = HudConfig(File(dir, "hud.json"))
    val shortcutsConfig: FileConfig = ShortcutsConfig(File(dir, "shortcuts.json"))
    val backgroundFile = File(dir, "userbackground.png")
    var firstStart = false

    /**
     * Setup folder
     */
    fun setupFolder() {
        if (!dir.exists()) {
            dir.mkdir()
            firstStart = true
        }
        if (!fontsDir.exists()) fontsDir.mkdir()
        if (!settingsDir.exists()) settingsDir.mkdir()
    }

    /**
     * Load all configs in file manager
     */
    fun loadAllConfigs() {
        for (field in javaClass.declaredFields) {
            if (field.type == FileConfig::class.java) {
                try {
                    if (!field.isAccessible) field.isAccessible = true
                    val fileConfig = field[this] as FileConfig
                    loadConfig(fileConfig)
                } catch (e: IllegalAccessException) {
                    LiquidCat.logger.error("Failed to load config file of field " + field.name + ".", e)
                }
            }
        }
    }

    /**
     * Load a list of configs
     *
     * @param configs list
     */
    fun loadConfigs(vararg configs: FileConfig) {
        for (fileConfig in configs) loadConfig(fileConfig)
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
     * Save all configs in file manager
     */
    fun saveAllConfigs() {
        for (field in javaClass.declaredFields) {
            if (field.type == FileConfig::class.java) {
                try {
                    if (!field.isAccessible) field.isAccessible = true
                    val fileConfig = field[this] as FileConfig
                    saveConfig(fileConfig)
                } catch (e: IllegalAccessException) {
                    LiquidCat.logger.error(
                        "[FileManager] Failed to save config file of field " +
                                field.name + ".", e
                    )
                }
            }
        }
    }

    /**
     * Save a list of configs
     *
     * @param configs list
     */
    fun saveConfigs(vararg configs: FileConfig) {
        for (fileConfig in configs) saveConfig(fileConfig)
    }

    /**
     * Save one config
     *
     * @param config to save
     */
    fun saveConfig(config: FileConfig) {
        saveConfig(config, false)
    }

    /**
     * Save one config
     *
     * @param config         to save
     * @param ignoreStarting check starting
     */
    private fun saveConfig(config: FileConfig, ignoreStarting: Boolean) {
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
     * Load background for background
     */
    fun loadBackground() {
        if (backgroundFile.exists()) {
            try {
                val bufferedImage: BufferedImage = ImageIO.read(FileInputStream(backgroundFile)) ?: return
                LiquidCat.background = ResourceLocation(LiquidCat.CLIENT_NAME.toLowerCase() + "/background.png")
                mc.textureManager.loadTexture(LiquidCat.background, DynamicTexture(bufferedImage))
                LiquidCat.logger.info("[FileManager] Loaded background.")
            } catch (e: Exception) {
                LiquidCat.logger.error("[FileManager] Failed to load background.", e)
            }
        }
    }

    companion object {
        val PRETTY_GSON = GsonBuilder().setPrettyPrinting().create()
    }

    /**
     * Constructor of file manager
     * Setup everything important
     */
    init {
        setupFolder()
        loadBackground()
    }
}