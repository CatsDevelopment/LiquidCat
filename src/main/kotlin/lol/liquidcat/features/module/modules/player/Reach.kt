/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.player

import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.value.FloatValue

object Reach : Module("Reach", "Increases your reach.", ModuleCategory.PLAYER) {

    val combatReach by FloatValue("CombatReach", 3.5f, 3f..7f)
    val buildReach by FloatValue("BuildReach", 5f, 4.5f..7f)

    val maxRange = combatReach.coerceAtLeast(buildReach)
}