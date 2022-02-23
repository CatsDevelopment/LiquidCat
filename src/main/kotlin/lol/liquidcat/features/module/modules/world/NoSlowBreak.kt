/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.world

import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.value.BoolValue

@ModuleInfo(
    "NoSlowBreak",
    "Automatically adjusts breaking speed when using modules that influence it.",
    ModuleCategory.WORLD
)
class NoSlowBreak : Module() {
    val airValue = BoolValue("Air", true)
    val waterValue = BoolValue("Water", false)
}