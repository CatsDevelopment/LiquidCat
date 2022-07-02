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
     */
    abstract fun load()

    /**
     * Save config to file
     */
    abstract fun save()

    /**
     * Create config file
     */
    fun create() = file.createNewFile()

    /**
     * Checks if a config file already exists
     */
    fun exists() = file.exists()
}