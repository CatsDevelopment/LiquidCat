/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.combat

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.entity.aiming
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntValue

object AutoBow : Module("AutoBow", "Automatically shoots an arrow whenever your bow is fully loaded.", ModuleCategory.COMBAT) {

    private val delay by IntValue("Delay", 20, 3..20)
    private val waitForBowAimbot by BoolValue("WaitForBowAimbot", true)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.aiming && mc.thePlayer.itemInUseDuration > delay && (!waitForBowAimbot || !BowAimbot.state || BowAimbot.hasTarget()))
            mc.playerController.onStoppedUsingItem(mc.thePlayer)
    }
}
