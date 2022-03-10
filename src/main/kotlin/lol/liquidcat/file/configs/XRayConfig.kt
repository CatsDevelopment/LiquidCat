/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.file.configs

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import lol.liquidcat.LiquidCat
import lol.liquidcat.LiquidCat.logger
import lol.liquidcat.file.FileConfig
import lol.liquidcat.file.FileManager
import lol.liquidcat.features.module.modules.render.XRay
import net.minecraft.block.Block
import java.io.*

class XRayConfig(file: File?) : FileConfig(file!!) {
    /**
     * Load config from file
     */
    override fun loadConfig() {
        val xRay = LiquidCat.moduleManager.getModule(XRay::class.java) as XRay?
        if (xRay == null) {
            logger.error("[FileManager] Failed to find xray module.")
            return
        }
        val jsonArray = JsonParser().parse(BufferedReader(FileReader(file))).asJsonArray
        xRay.xrayBlocks.clear()
        for (jsonElement in jsonArray) {
            try {
                val block = Block.getBlockFromName(jsonElement.asString)
                if (xRay.xrayBlocks.contains(block)) {
                    logger.error("[FileManager] Skipped xray block '" + block.registryName + "' because the block is already added.")
                    continue
                }
                xRay.xrayBlocks.add(block)
            } catch (throwable: Throwable) {
                logger.error("[FileManager] Failed to add block to xray.", throwable)
            }
        }
    }

    /**
     * Save config to file
     */
    override fun saveConfig() {
        val xRay = LiquidCat.moduleManager.getModule(XRay::class.java) as XRay?
        if (xRay == null) {
            logger.error("[FileManager] Failed to find xray module.")
            return
        }
        val jsonArray = JsonArray()
        for (block in xRay.xrayBlocks) jsonArray.add(FileManager.PRETTY_GSON.toJsonTree(Block.getIdFromBlock(block)))
        val printWriter = PrintWriter(FileWriter(file))
        printWriter.println(FileManager.PRETTY_GSON.toJson(jsonArray))
        printWriter.close()
    }
}