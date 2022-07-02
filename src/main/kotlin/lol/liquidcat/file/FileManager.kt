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

    val configs = arrayOf(
        ModulesConfig,
        ValuesConfig,
        ClickGuiConfig,
        AccountsConfig,
        FriendsConfig,
        XRayConfig,
        HudConfig,
        ShortcutsConfig
    )

    val gson = GsonBuilder().setPrettyPrinting().create()!!

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
            LiquidCat.logger.info("[FileManager] Skipped loading config: ${config.file.name}.")
            saveConfig(config, true)
            return
        }

        runCatching {
            config.load()
            LiquidCat.logger.info("[FileManager] Loaded config: ${config.file.name}.")
        }.onFailure {
            LiquidCat.logger.error("[FileManager] Failed to load config file: ${config.file.name}.", it)
        }
    }

    /**
     * Save [config]
     *
     * @param ignoreStarting check starting
     */
    @JvmOverloads
    @JvmStatic
    fun saveConfig(config: FileConfig, ignoreStarting: Boolean = false) {
        if (!ignoreStarting && LiquidCat.loading) return

        runCatching {
            if (!config.exists()) config.create()
            config.save()
            LiquidCat.logger.info("[FileManager] Saved config: ${config.file.name}.")
        }.onFailure {
            LiquidCat.logger.error("[FileManager] Failed to save config file: ${config.file.name}.", it)
        }
    }
}