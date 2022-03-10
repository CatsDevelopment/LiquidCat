/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.file

import java.io.File

abstract class FileConfig(val file: File) {

    /**
     * Load config from file
     *
     */
    abstract fun loadConfig()

    /**
     * Save config to file
     */
    abstract fun saveConfig()

    /**
     * Create config
     */
    fun createConfig() {
        file.createNewFile()
    }

    /**
     * @return config file exist
     */
    fun hasConfig(): Boolean {
        return file.exists()
    }
}