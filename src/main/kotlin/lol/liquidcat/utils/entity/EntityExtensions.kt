package lol.liquidcat.utils.entity

import lol.liquidcat.utils.mc
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.potion.Potion
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

val EntityPlayerSP.moving: Boolean
    get() = movementInput.moveForward != 0f || movementInput.moveStrafe != 0f

val EntityPlayer.ping: Int
    get() = mc.netHandler.getPlayerInfo(uniqueID)?.responseTime ?: 0

val EntityPlayerSP.minFallDistance: Float
    get() = if (isPotionActive(Potion.jump)) 3f + getActivePotionEffect(Potion.jump).amplifier + 1 else 3f

val Entity.sqrtSpeed: Double
    get() = sqrt(motionX * motionX + motionZ * motionZ)

fun EntityPlayerSP.strafe(yaw: Float = directionYaw, speed: Double = sqrtSpeed) {
    if (!moving) {
        motionX = 0.0
        motionZ = 0.0

        return
    }

    val angle = Math.toRadians(yaw.toDouble())

    motionX = -sin(angle) * speed
    motionZ = cos(angle) * speed
}

val EntityPlayerSP.directionYaw: Float
    get() {
        var rotationYaw = rotationYaw
        var forward = 1f

        if (moveForward < 0f) {
            rotationYaw += 180f
            forward = -0.5f

        } else if (moveForward > 0f)
            forward = 0.5f

        if (moveStrafing > 0f)
            rotationYaw -= 90f * forward

        if (moveStrafing < 0f)
            rotationYaw += 90f * forward

        return rotationYaw
    }