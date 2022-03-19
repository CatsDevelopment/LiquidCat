/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.combat

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.StrafeEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.Rotation
import lol.liquidcat.utils.entity.EntityUtils
import lol.liquidcat.utils.entity.getDistanceToEntityBox
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import net.ccbluex.liquidbounce.utils.RotationUtils
import lol.liquidcat.utils.timer.MSTimer
import kotlin.random.Random

//TODO Rewrite?

class Aimbot : Module("Aimbot", "Automatically faces selected entities around you.", ModuleCategory.COMBAT) {

    private val rangeValue = FloatValue("Range", 4.4f, 1f..8f)
    private val turnSpeedValue = FloatValue("TurnSpeed", 2f, 1f..180f)
    private val fovValue = FloatValue("FOV", 180f, 1f..180f)
    private val centerValue = BoolValue("Center", false)
    private val lockValue = BoolValue("Lock", true)
    private val onClickValue = BoolValue("OnClick", false)
    private val jitterValue = BoolValue("Jitter", false)

    private val clickTimer = MSTimer()

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (mc.gameSettings.keyBindAttack.isKeyDown)
            clickTimer.reset()

        if (onClickValue.get() && clickTimer.hasTimePassed(500L))
            return

        val range = rangeValue.get()
        val entity = mc.theWorld.loadedEntityList
                .filter {
                    EntityUtils.isSelected(it, true) && mc.thePlayer.canEntityBeSeen(it) &&
                            mc.thePlayer.getDistanceToEntityBox(it) <= range && RotationUtils.getRotationDifference(it) <= fovValue.get()
                }
                .minBy { RotationUtils.getRotationDifference(it) } ?: return

        if (!lockValue.get() && RotationUtils.isFaced(entity, range.toDouble()))
            return

        val rotation = RotationUtils.limitAngleChange(
                Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch),
                if (centerValue.get())
                    RotationUtils.toRotation(RotationUtils.getCenter(entity.entityBoundingBox), true)
                else
                    RotationUtils.searchCenter(entity.entityBoundingBox, false, false, true,
                            false).rotation,
                (turnSpeedValue.get() + Math.random()).toFloat()
        )

        rotation.toPlayer(mc.thePlayer)

        if (jitterValue.get()) {
            val yaw = Random.nextBoolean()
            val pitch = Random.nextBoolean()
            val yawNegative = Random.nextBoolean()
            val pitchNegative = Random.nextBoolean()

            if (yaw)
                mc.thePlayer.rotationYaw += if (yawNegative) -Random.nextFloat() else Random.nextFloat()

            if (pitch) {
                mc.thePlayer.rotationPitch += if (pitchNegative) -Random.nextFloat() else Random.nextFloat()
                if (mc.thePlayer.rotationPitch > 90)
                    mc.thePlayer.rotationPitch = 90F
                else if (mc.thePlayer.rotationPitch < -90)
                    mc.thePlayer.rotationPitch = -90F
            }
        }
    }
}