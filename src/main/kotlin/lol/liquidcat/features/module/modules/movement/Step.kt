/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.movement

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.StepConfirmEvent
import lol.liquidcat.event.StepEvent
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.entity.jumpHeight
import lol.liquidcat.utils.sendPacket
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.ListValue
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition

// TODO Make more height for NCP

object Step : Module("Step", "Allows you to step up blocks.", ModuleCategory.MOVEMENT) {

    private val mode by ListValue("Mode", arrayOf("Vanilla", "NCP", "Matrix"), "NCP")
    private val height by FloatValue("Height", 1f, 0.6f..10f)
    private val timer by FloatValue("Timer", 0.3f, 0.1f..1.0f)

    private var usedTimer = false

    override val tag
        get() = mode

    override fun onDisable() {
        if (usedTimer) {
            mc.timer.timerSpeed = 1f
            usedTimer = false
        }
    }

    @EventTarget
    fun onStep(event: StepEvent) {
        when (mode) {
            "Vanilla" -> event.stepHeight = height

            else -> if (!mc.thePlayer.isInWeb && mc.thePlayer.onGround)
                event.stepHeight = 1f
        }
    }

    @EventTarget
    fun onStepConfirm(event: StepConfirmEvent) {

        val x = mc.thePlayer.posX
        val y = mc.thePlayer.posY
        val z = mc.thePlayer.posZ

        val stepHeight = mc.thePlayer.entityBoundingBox.minY - y

        if (stepHeight > 0.6) {
            mc.timer.timerSpeed = timer

            when (mode) {
                "NCP" -> {
                    sendPacket(C04PacketPlayerPosition(x, y + 0.41999998688698 * stepHeight, z, false))
                    sendPacket(C04PacketPlayerPosition(x, y + 0.7531999805212 * stepHeight, z, false))
                }

                "Matrix" -> sendPacket(C04PacketPlayerPosition(x, y + mc.thePlayer.jumpHeight, z, true))
            }
            usedTimer = true
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (usedTimer) {
            mc.timer.timerSpeed = 1f
            usedTimer = false
        }
    }
}