/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.file.configs

import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import lol.liquidcat.LiquidCat
import lol.liquidcat.LiquidCat.logger
import lol.liquidcat.file.FileConfig
import lol.liquidcat.file.FileManager
import net.ccbluex.liquidbounce.ui.client.clickgui.elements.ModuleElement
import java.io.*

class ClickGuiConfig(file: File?) : FileConfig(file!!) {
    /**
     * Load config from file
     */
    override fun loadConfig() {
        val jsonElement = JsonParser().parse(BufferedReader(FileReader(file)))
        if (jsonElement is JsonNull) return
        val jsonObject = jsonElement as JsonObject
        for (panel in LiquidCat.clickGui.panels) {
            if (!jsonObject.has(panel.name)) continue
            try {
                val panelObject = jsonObject.getAsJsonObject(panel.name)
                panel.open = panelObject["open"].asBoolean
                panel.isVisible = panelObject["visible"].asBoolean
                panel.setX(panelObject["posX"].asInt)
                panel.setY(panelObject["posY"].asInt)
                for (element in panel.elements) {
                    if (element !is ModuleElement) continue
                    val moduleElement = element
                    if (!panelObject.has(moduleElement.module.name)) continue
                    try {
                        val elementObject = panelObject.getAsJsonObject(moduleElement.module.name)
                        moduleElement.isShowSettings = elementObject["Settings"].asBoolean
                    } catch (e: Exception) {
                        logger.error(
                            "Error while loading clickgui module element with the name '" + moduleElement.module.name + "' (Panel Name: " + panel.name + ").",
                            e
                        )
                    }
                }
            } catch (e: Exception) {
                logger.error("Error while loading clickgui panel with the name '" + panel.name + "'.", e)
            }
        }
    }

    /**
     * Save config to file
     */
    override fun saveConfig() {
        val jsonObject = JsonObject()
        for (panel in LiquidCat.clickGui.panels) {
            val panelObject = JsonObject()
            panelObject.addProperty("open", panel.open)
            panelObject.addProperty("visible", panel.isVisible)
            panelObject.addProperty("posX", panel.getX())
            panelObject.addProperty("posY", panel.getY())
            for (element in panel.elements) {
                if (element !is ModuleElement) continue
                val moduleElement = element
                val elementObject = JsonObject()
                elementObject.addProperty("Settings", moduleElement.isShowSettings)
                panelObject.add(moduleElement.module.name, elementObject)
            }
            jsonObject.add(panel.name, panelObject)
        }
        val printWriter = PrintWriter(FileWriter(file))
        printWriter.println(FileManager.PRETTY_GSON.toJson(jsonObject))
        printWriter.close()
    }
}