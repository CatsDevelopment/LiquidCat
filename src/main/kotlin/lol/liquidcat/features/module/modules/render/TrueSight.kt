/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.render

import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.value.BoolValue

object TrueSight : Module("TrueSight", "Allows you to see invisible entities and barriers.", ModuleCategory.RENDER) {
    val barriers by BoolValue("Barriers", true)
    val entities by BoolValue("Entities", true)
}