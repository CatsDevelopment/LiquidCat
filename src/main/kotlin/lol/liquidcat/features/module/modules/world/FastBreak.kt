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
import lol.liquidcat.value.FloatValue

object FastBreak : Module("FastBreak", "Allows you to break blocks faster.", ModuleCategory.WORLD) {

    private val damage by FloatValue("BreakDamage", 0.8f, 0.1f..1f)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.playerController.blockHitDelay = 0

        if (mc.playerController.curBlockDamageMP > damage)
            mc.playerController.curBlockDamageMP = 1f

        if (Fucker.currentDamage > damage)
            Fucker.currentDamage = 1f

        if (Nuker.currentDamage > damage)
            Nuker.currentDamage = 1f
    }
}
