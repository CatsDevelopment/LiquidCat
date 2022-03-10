/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.combat

import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.value.FloatValue

class HitBox : Module("HitBox", "Makes hitboxes of targets bigger.", ModuleCategory.COMBAT) {
    val sizeValue = FloatValue("Size", 0.5f, 0f, 1f)
}