/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.world

import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.value.IntValue

class FastPlace : Module("FastPlace", "Allows you to place blocks faster.", ModuleCategory.WORLD) {
    val speed by IntValue("Speed", 0, 0..4)
}