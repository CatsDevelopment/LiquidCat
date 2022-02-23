/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.player

import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.value.FloatValue

@ModuleInfo("Reach", "Increases your reach.", ModuleCategory.PLAYER)
class Reach : Module() {

    val combatReachValue = FloatValue("CombatReach", 3.5f, 3f, 7f)
    val buildReachValue = FloatValue("BuildReach", 5f, 4.5f, 7f)

    val maxRange = combatReachValue.get().coerceAtLeast(buildReachValue.get())
}