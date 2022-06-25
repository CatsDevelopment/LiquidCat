/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.file.configs

import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import lol.liquidcat.features.module.ModuleManager
import lol.liquidcat.file.FileConfig
import lol.liquidcat.file.FileManager
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

object ModulesConfig : FileConfig(File(FileManager.mainDir, "modules.json")) {

    override fun load() {
        val jsonElement = JsonParser().parse(file.bufferedReader())

        if (jsonElement is JsonNull)
            return

        for (e in jsonElement.asJsonObject.entrySet()) {
            val module = ModuleManager.getModule(e.key)

            if (module != null) {
                val jsonModule = e.value.asJsonObject

                module.state = jsonModule["State"].asBoolean
                module.keyBind = jsonModule["KeyBind"].asInt
                module.hide = jsonModule["Hidden"].asBoolean

                val jsonSettings = jsonModule["Settings"].asJsonObject

                for (value in module.values) {
                    val element = jsonSettings[value.name]

                    if (element != null)
                        value.fromJson(element)
                }
            }
        }
    }

    override fun save() {
        val jsonObject = JsonObject()

        for (module in ModuleManager.modules) {
            val jsonMod = JsonObject()

            jsonMod.addProperty("State", module.state)
            jsonMod.addProperty("KeyBind", module.keyBind)
            jsonMod.addProperty("Hidden", module.hide)

            val jsonSettings = JsonObject()
            for (value in module.values) {
                jsonSettings.add(value.name, value.toJson())
            }

            jsonMod.add("Settings", jsonSettings)

            jsonObject.add(module.name, jsonMod)
        }

        val printWriter = PrintWriter(FileWriter(file))
        printWriter.println(FileManager.gson.toJson(jsonObject))
        printWriter.close()
    }
}