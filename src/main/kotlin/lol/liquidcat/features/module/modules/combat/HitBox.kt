/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package lol.liquidcat.features.module.modules.combat

import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.value.FloatValue

@ModuleInfo(name = "HitBox", description = "Makes hitboxes of targets bigger.", category = ModuleCategory.COMBAT)
class HitBox : Module() {
    val sizeValue = FloatValue("Size", 0.5F, 0F, 1F)
}