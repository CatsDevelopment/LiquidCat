/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.world

import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.value.BoolValue

object NoSlowBreak : Module("NoSlowBreak", "Automatically adjusts breaking speed when using modules that influence it.", ModuleCategory.WORLD) {
    val air by BoolValue("Air", true)
    val water by BoolValue("Water", false)
}