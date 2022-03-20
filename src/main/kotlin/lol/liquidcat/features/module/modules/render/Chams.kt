/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.render

import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.value.BoolValue

class Chams : Module("Chams", "Allows you to see targets through blocks.", ModuleCategory.RENDER) {
    val targets by BoolValue("Targets", true)
    val chests by BoolValue("Chests", true)
    val items by BoolValue("Items", true)
}