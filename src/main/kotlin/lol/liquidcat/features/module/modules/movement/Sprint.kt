/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.movement

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.Rotation
import lol.liquidcat.utils.entity.moving
import lol.liquidcat.value.BoolValue
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.minecraft.potion.Potion

//TODO Rewrite

object Sprint : Module("Sprint", "Automatically sprints all the time.", ModuleCategory.MOVEMENT) {

    val allDirections by BoolValue("AllDirections", true)
    private val blindness by BoolValue("Blindness", true)
    val food by BoolValue("Food", true)
    val checkServerSide by BoolValue("CheckServerSide", false)
    val checkServerSideGround by BoolValue("CheckServerSideOnlyGround", false)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!mc.thePlayer.moving || mc.thePlayer.isSneaking || blindness && mc.thePlayer.isPotionActive(Potion.blindness) ||
            food && !(mc.thePlayer.foodStats.foodLevel > 6.0f || mc.thePlayer.capabilities.allowFlying) ||
            (checkServerSide && (mc.thePlayer.onGround || !checkServerSideGround) &&
                    !allDirections && RotationUtils.targetRotation != null &&
                    RotationUtils.getRotationDifference(Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)) > 30)) {
            mc.thePlayer.isSprinting = false
            return
        }
        if (allDirections || mc.thePlayer.movementInput.moveForward >= 0.8f)
            mc.thePlayer.isSprinting = true
    }
}