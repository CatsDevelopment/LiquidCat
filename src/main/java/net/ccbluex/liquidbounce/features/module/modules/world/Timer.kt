/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.world

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.event.WorldEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue

@ModuleInfo(name = "Timer", description = "Changes the speed of the entire game.", category = ModuleCategory.WORLD)
class Timer : Module() {

    private val speedValue = FloatValue("Speed", 2F, 0.1F, 10F)
    private val onMoveValue = BoolValue("OnMove", true)

    override fun onDisable() {
        if (mc.thePlayer == null)
            return

        mc.timer.timerSpeed = 1F
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if(MovementUtils.isMoving() || !onMoveValue.get()) {
            mc.timer.timerSpeed = speedValue.get()
            return
        }

        mc.timer.timerSpeed = 1F
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        if (event.worldClient != null)
            return

        state = false
    }
}
