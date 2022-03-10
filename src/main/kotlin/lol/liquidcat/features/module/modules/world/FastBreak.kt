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

class FastBreak : Module("FastBreak", "Allows you to break blocks faster.", ModuleCategory.WORLD) {

    private val breakDamage = FloatValue("BreakDamage", 0.8f, 0.1f, 1f)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.playerController.blockHitDelay = 0

        if (mc.playerController.curBlockDamageMP > breakDamage.get())
            mc.playerController.curBlockDamageMP = 1f

        if (Fucker.currentDamage > breakDamage.get())
            Fucker.currentDamage = 1f

        if (Nuker.currentDamage > breakDamage.get())
            Nuker.currentDamage = 1f
    }
}
