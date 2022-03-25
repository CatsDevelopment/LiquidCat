/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.file.configs

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import lol.liquidcat.LiquidCat
import lol.liquidcat.features.module.ModuleManager
import lol.liquidcat.file.FileConfig
import lol.liquidcat.file.FileManager
import java.io.*

class ModulesConfig(file: File?) : FileConfig(file!!) {
    /**
     * Load config from file
     */
    override fun loadConfig() {
        val jsonElement = JsonParser().parse(BufferedReader(FileReader(file)))
        if (jsonElement is JsonNull) return
        val entryIterator: Iterator<Map.Entry<String, JsonElement>> = jsonElement.asJsonObject.entrySet().iterator()
        while (entryIterator.hasNext()) {
            val (key, value) = entryIterator.next()
            val module = ModuleManager.getModule(key)
            if (module != null) {
                val jsonModule = value as JsonObject
                module.state = jsonModule["State"].asBoolean
                module.keyBind = jsonModule["KeyBind"].asInt
                if (jsonModule.has("Hidden")) module.hide = jsonModule["Hidden"].asBoolean
            }
        }
    }

    /**
     * Save config to file
     *
     */
    override fun saveConfig() {
        val jsonObject = JsonObject()
        for (module in ModuleManager.modules) {
            val jsonMod = JsonObject()
            jsonMod.addProperty("State", module.state)
            jsonMod.addProperty("KeyBind", module.keyBind)
            jsonMod.addProperty("Hidden", module.hide)
            jsonObject.add(module.name, jsonMod)
        }
        val printWriter = PrintWriter(FileWriter(file))
        printWriter.println(FileManager.PRETTY_GSON.toJson(jsonObject))
        printWriter.close()
    }
}