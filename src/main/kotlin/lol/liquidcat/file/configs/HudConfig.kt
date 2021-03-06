/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.file.configs

import lol.liquidcat.LiquidCat
import lol.liquidcat.file.FileConfig
import lol.liquidcat.file.FileManager
import lol.liquidcat.ui.client.hud.Config
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

object HudConfig : FileConfig(File(FileManager.mainDir, "hud.json")) {
    /**
     * Load config from file
     */
    override fun load() {
        LiquidCat.hud.clearElements()
        LiquidCat.hud = Config(FileUtils.readFileToString(file)).toHUD()
    }

    /**
     * Save config to file
     */
    override fun save() {
        val printWriter = PrintWriter(FileWriter(file))
        printWriter.println(Config(LiquidCat.hud).toJson())
        printWriter.close()
    }
}