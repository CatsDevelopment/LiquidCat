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
import lol.liquidcat.value.BoolValue

object SafeWalk : Module("SafeWalk", "Prevents you from falling down as if you were sneaking.", ModuleCategory.MOVEMENT) {

    private val airSafe by BoolValue("AirSafe", false)

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (airSafe || mc.thePlayer.onGround) event.isSafeWalk = true
    }
}
