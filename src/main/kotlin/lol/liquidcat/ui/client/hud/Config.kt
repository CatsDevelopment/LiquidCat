/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.ui.client.hud

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import lol.liquidcat.LiquidCat
import lol.liquidcat.ui.client.hud.HUD.Companion.createDefault
import lol.liquidcat.ui.client.hud.HUD.Companion.elements
import lol.liquidcat.ui.client.hud.element.ElementInfo
import lol.liquidcat.ui.client.hud.element.Align
import lol.liquidcat.value.FontValue

class Config {

    private var jsonArray = JsonArray()

    constructor(config: String) {
        jsonArray = Gson().fromJson(config, JsonArray::class.java)
    }

    constructor(hud: HUD) {
        for (element in hud.elements) {
            val elementObject = JsonObject()
            elementObject.addProperty("Type", element.name)
            elementObject.addProperty("X", element.x)
            elementObject.addProperty("Y", element.y)
            elementObject.addProperty("Scale", element.scale)
            elementObject.addProperty("HorizontalFacing", element.align.horizontal.title)
            elementObject.addProperty("VerticalFacing", element.align.vertical.title)

            for (value in element.values)
                elementObject.add(value.name, value.toJson())

            jsonArray.add(elementObject)
        }
    }

    fun toJson(): String = GsonBuilder().setPrettyPrinting().create().toJson(jsonArray)

    fun toHUD(): HUD {
        val hud = HUD()

        try {
            for (jsonObject in jsonArray) {
                try {
                    if (jsonObject !is JsonObject)
                        continue

                    if (!jsonObject.has("Type"))
                        continue

                    val type = jsonObject["Type"].asString

                    for (elementClass in elements) {
                        val classType = elementClass.getAnnotation(ElementInfo::class.java).name

                        if (classType == type) {
                            val element = elementClass.newInstance()

                            element.x = jsonObject["X"].asInt.toDouble()
                            element.y = jsonObject["Y"].asInt.toDouble()
                            element.scale = jsonObject["Scale"].asFloat
                            element.align = Align(
                                    Align.Horizontal.byName(jsonObject["HorizontalFacing"].asString)!!,
                                    Align.Vertical.byName(jsonObject["VerticalFacing"].asString)!!
                            )

                            for (value in element.values) {
                                if (jsonObject.has(value.name))
                                    value.fromJson(jsonObject[value.name])
                            }

                            // Support for old HUD files
                            if (jsonObject.has("font"))
                                element.values.find { it is FontValue }?.fromJson(jsonObject["font"])

                            hud.addElement(element)
                            break
                        }
                    }
                } catch (e: Exception) {
                    LiquidCat.logger.error("Error while loading custom hud element from config.", e)
                }
            }

            // Add forced elements when missing
            for (elementClass in elements) {
                if (elementClass.getAnnotation(ElementInfo::class.java).force
                        && hud.elements.none { it.javaClass == elementClass }) {
                    hud.addElement(elementClass.newInstance())
                }
            }
        } catch (e: Exception) {
            LiquidCat.logger.error("Error while loading custom hud config.", e)
            return createDefault()
        }

        return hud
    }
}