/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.movement

import lol.liquidcat.event.EventState
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.MotionEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.utils.entity.strafe

@ModuleInfo(name = "Strafe", description = "Allows you to freely move in mid air.", category = ModuleCategory.MOVEMENT)
class Strafe : Module() {

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.PRE)
            mc.thePlayer.strafe()
    }
}