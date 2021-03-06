/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.world

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.entity.moving
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue

object Timer : Module("Timer", "Changes the speed of the entire game.", ModuleCategory.WORLD) {

    private val speed by FloatValue("Speed", 2f, 0.1f..10f)
    private val onMove by BoolValue("OnMove", true)

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.timer.timerSpeed = if (mc.thePlayer.moving || !onMove) speed else 1f
    }
}