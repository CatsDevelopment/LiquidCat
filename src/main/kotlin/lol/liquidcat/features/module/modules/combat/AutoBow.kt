/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.combat

import lol.liquidcat.LiquidCat
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.utils.entity.aiming
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntegerValue

@ModuleInfo("AutoBow", "Automatically shoots an arrow whenever your bow is fully loaded.", ModuleCategory.COMBAT)
class AutoBow : Module() {

    private val delayValue = IntegerValue("Delay", 20, 3, 20)
    private val waitForBowAimbot = BoolValue("WaitForBowAimbot", true)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val bowAimbot = LiquidCat.moduleManager[BowAimbot::class.java] as BowAimbot

        if (mc.thePlayer.aiming && mc.thePlayer.itemInUseDuration > delayValue.get() && (!waitForBowAimbot.get() || !bowAimbot.state || bowAimbot.hasTarget()))
            mc.playerController.onStoppedUsingItem(mc.thePlayer)
    }
}
