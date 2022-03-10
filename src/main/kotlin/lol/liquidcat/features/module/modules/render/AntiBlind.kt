/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.render

import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.value.BoolValue

class AntiBlind : Module("AntiBlind", "Cancels blindness effects.", ModuleCategory.RENDER) {
    val confusionEffect = BoolValue("Confusion", true)
    val pumpkinEffect = BoolValue("Pumpkin", true)
    val fireEffect = BoolValue("Fire", false)
}