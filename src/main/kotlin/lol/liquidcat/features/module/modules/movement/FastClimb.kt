/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.movement

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.MoveEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.value.FloatValue

object FastClimb : Module("FastClimb", "Allows you to climb up ladders and vines faster.", ModuleCategory.MOVEMENT) {

    private val speed by FloatValue("Speed", 0.2872f, 0.01f..5f)

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (mc.thePlayer.isOnLadder)
            event.y = if (mc.thePlayer.isCollidedHorizontally) speed.toDouble() else -speed.toDouble()
    }
}