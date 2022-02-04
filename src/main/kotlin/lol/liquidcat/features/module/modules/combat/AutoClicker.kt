/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package lol.liquidcat.features.module.modules.combat

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Render3DEvent
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntegerValue
import net.minecraft.client.settings.KeyBinding
import kotlin.random.Random

@ModuleInfo(name = "AutoClicker", description = "Constantly clicks when holding down a mouse button.", category = ModuleCategory.COMBAT)
class AutoClicker : Module() {
    private val maxCPSValue: IntegerValue = object : IntegerValue("MaxCPS", 8, 1, 20) {

        override fun onChanged(oldValue: Int, newValue: Int) {
            val minCPS = minCPSValue.get()
            if (minCPS > newValue)
                set(minCPS)
        }

    }

    private val minCPSValue: IntegerValue = object : IntegerValue("MinCPS", 5, 1, 20) {

        override fun onChanged(oldValue: Int, newValue: Int) {
            val maxCPS = maxCPSValue.get()
            if (maxCPS < newValue)
                set(maxCPS)
        }

    }

    private val rightValue = BoolValue("Right", true)
    private val leftValue = BoolValue("Left", true)
    private val jitterValue = BoolValue("Jitter", false)

    private var rightDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get())
    private var rightLastSwing = 0L
    private var leftDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get())
    private var leftLastSwing = 0L

    @EventTarget
    fun onRender(event: Render3DEvent) {
        // Left click
        if (mc.gameSettings.keyBindAttack.isKeyDown && leftValue.get() &&
                System.currentTimeMillis() - leftLastSwing >= leftDelay) {
            KeyBinding.onTick(mc.gameSettings.keyBindAttack.keyCode) // Minecraft Click Handling

            leftLastSwing = System.currentTimeMillis()
            leftDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get())
        }

        // Right click
        if (mc.gameSettings.keyBindUseItem.isKeyDown && !mc.thePlayer.isUsingItem && rightValue.get() &&
                System.currentTimeMillis() - rightLastSwing >= rightDelay) {
            KeyBinding.onTick(mc.gameSettings.keyBindUseItem.keyCode) // Minecraft Click Handling

            rightLastSwing = System.currentTimeMillis()
            rightDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get())
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (jitterValue.get() && (leftValue.get() && mc.gameSettings.keyBindAttack.isKeyDown || rightValue.get() && mc.gameSettings.keyBindUseItem.isKeyDown && !mc.thePlayer.isUsingItem)) {
            if (Random.nextBoolean()) mc.thePlayer.rotationYaw += if (Random.nextBoolean()) -RandomUtils.nextFloat(0F, 1F) else RandomUtils.nextFloat(0F, 1F)

            if (Random.nextBoolean()) {
                mc.thePlayer.rotationPitch += if (Random.nextBoolean()) -RandomUtils.nextFloat(0F, 1F) else RandomUtils.nextFloat(0F, 1F)

                // Make sure pitch is not going into unlegit values
                if (mc.thePlayer.rotationPitch > 90)
                    mc.thePlayer.rotationPitch = 90F
                else if (mc.thePlayer.rotationPitch < -90)
                    mc.thePlayer.rotationPitch = -90F
            }
        }
    }
}