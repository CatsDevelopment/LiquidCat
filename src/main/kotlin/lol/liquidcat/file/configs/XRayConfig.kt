/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.file.configs

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import lol.liquidcat.LiquidCat.logger
import lol.liquidcat.features.module.modules.render.XRay
import lol.liquidcat.file.FileConfig
import lol.liquidcat.file.FileManager
import net.minecraft.block.Block
import java.io.*

object XRayConfig : FileConfig(File(FileManager.mainDir, "xray-blocks.json")) {

    override fun load() {
        val jsonArray = JsonParser().parse(BufferedReader(FileReader(file))).asJsonArray
        XRay.xrayBlocks.clear()
        for (jsonElement in jsonArray) {
            try {
                val block = Block.getBlockFromName(jsonElement.asString)
                if (XRay.xrayBlocks.contains(block)) {
                    logger.error("[FileManager] Skipped xray block '" + block.registryName + "' because the block is already added.")
                    continue
                }
                XRay.xrayBlocks.add(block)
            } catch (throwable: Throwable) {
                logger.error("[FileManager] Failed to add block to xray.", throwable)
            }
        }
    }

    override fun save() {
        val jsonArray = JsonArray()
        for (block in XRay.xrayBlocks) jsonArray.add(FileManager.gson.toJsonTree(Block.getIdFromBlock(block)))
        val printWriter = PrintWriter(FileWriter(file))
        printWriter.println(FileManager.gson.toJson(jsonArray))
        printWriter.close()
    }
}