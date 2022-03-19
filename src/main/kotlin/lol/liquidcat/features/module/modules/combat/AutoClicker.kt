/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.combat

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Render3DEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntValue
import lol.liquidcat.utils.timer.TimeUtils
import net.minecraft.client.settings.KeyBinding

class AutoClicker : Module("AutoClicker", "Constantly clicks when holding down a mouse button.", ModuleCategory.COMBAT) {

    private val rightValue = BoolValue("Right", true)
    private val leftValue = BoolValue("Left", true)

    private val maxCPSValue: IntValue = object : IntValue("MaxCPS", 8, 1..20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val minCPS = minCPSValue.get()

            if (minCPS > newValue) set(minCPS)
        }
    }

    private val minCPSValue: IntValue = object : IntValue("MinCPS", 5, 1..20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val maxCPS = maxCPSValue.get()

            if (maxCPS < newValue) set(maxCPS)
        }
    }

    private var rightDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get())
    private var rightLastSwing = 0L

    private var leftDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get())
    private var leftLastSwing = 0L

    @EventTarget
    fun onRender(event: Render3DEvent) {

        // Left click
        if (leftValue.get() && mc.gameSettings.keyBindAttack.isKeyDown && System.currentTimeMillis() - leftLastSwing >= leftDelay) {
            KeyBinding.onTick(mc.gameSettings.keyBindAttack.keyCode)

            leftLastSwing = System.currentTimeMillis()
            leftDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get())
        }

        // Right click
        if (rightValue.get() && mc.gameSettings.keyBindUseItem.isKeyDown && !mc.thePlayer.isUsingItem && System.currentTimeMillis() - rightLastSwing >= rightDelay) {
            KeyBinding.onTick(mc.gameSettings.keyBindUseItem.keyCode)

            rightLastSwing = System.currentTimeMillis()
            rightDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get())
        }
    }
}