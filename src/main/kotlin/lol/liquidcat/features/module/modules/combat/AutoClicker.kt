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

    private val right by BoolValue("Right", true)
    private val left by BoolValue("Left", true)

    private val maxCPS: Int by object : IntValue("MaxCPS", 8, 1..20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val minCPS = minCPS

            if (minCPS > newValue) set(minCPS)
        }
    }

    private val minCPS: Int by object : IntValue("MinCPS", 5, 1..20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val maxCPS = maxCPS

            if (maxCPS < newValue) set(maxCPS)
        }
    }

    private var rightDelay = TimeUtils.randomClickDelay(minCPS, maxCPS)
    private var rightLastSwing = 0L

    private var leftDelay = TimeUtils.randomClickDelay(minCPS, maxCPS)
    private var leftLastSwing = 0L

    @EventTarget
    fun onRender(event: Render3DEvent) {

        // Left click
        if (left && mc.gameSettings.keyBindAttack.isKeyDown && System.currentTimeMillis() - leftLastSwing >= leftDelay) {
            KeyBinding.onTick(mc.gameSettings.keyBindAttack.keyCode)

            leftLastSwing = System.currentTimeMillis()
            leftDelay = TimeUtils.randomClickDelay(minCPS, maxCPS)
        }

        // Right click
        if (right && mc.gameSettings.keyBindUseItem.isKeyDown && !mc.thePlayer.isUsingItem && System.currentTimeMillis() - rightLastSwing >= rightDelay) {
            KeyBinding.onTick(mc.gameSettings.keyBindUseItem.keyCode)

            rightLastSwing = System.currentTimeMillis()
            rightDelay = TimeUtils.randomClickDelay(minCPS, maxCPS)
        }
    }
}