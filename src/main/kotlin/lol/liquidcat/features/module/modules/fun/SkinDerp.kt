/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.`fun`

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntegerValue
import net.minecraft.entity.player.EnumPlayerModelParts
import kotlin.random.Random

@ModuleInfo("SkinDerp", "Makes your skin blink (Requires multi-layer skin).", ModuleCategory.FUN)
class SkinDerp : Module() {

    private val hatValue = BoolValue("Hat", true)
    private val jacketValue = BoolValue("Jacket", true)
    private val leftPantsValue = BoolValue("LeftPants", true)
    private val rightPantsValue = BoolValue("RightPants", true)
    private val leftSleeveValue = BoolValue("LeftSleeve", true)
    private val rightSleeveValue = BoolValue("RightSleeve", true)

    private val delayValue = IntegerValue("Delay", 0, 0, 1000)

    private var prevModelParts = emptySet<EnumPlayerModelParts>()

    private val delayTimer = MSTimer()

    override fun onEnable() {
        prevModelParts = mc.gameSettings.modelParts
    }

    override fun onDisable() {

        // Disable all current model parts
        mc.gameSettings.modelParts.forEach { mc.gameSettings.setModelPartEnabled(it, false) }

        // Enable all old model parts
        prevModelParts.forEach { mc.gameSettings.setModelPartEnabled(it, true) }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (delayTimer.hasTimePassed(delayValue.get().toLong())) {
            if (hatValue.get())
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.HAT, Random.nextBoolean())
            if (jacketValue.get())
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.JACKET, Random.nextBoolean())
            if (leftPantsValue.get())
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_PANTS_LEG, Random.nextBoolean())
            if (rightPantsValue.get())
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_PANTS_LEG, Random.nextBoolean())
            if (leftSleeveValue.get())
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_SLEEVE, Random.nextBoolean())
            if (rightSleeveValue.get())
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_SLEEVE, Random.nextBoolean())

            delayTimer.reset()
        }
    }
}