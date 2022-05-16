/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.entity

import lol.liquidcat.features.friend.FriendManager
import lol.liquidcat.utils.mc
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.utils.toRadians
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraft.entity.boss.EntityDragon
import net.minecraft.entity.monster.EntityGhast
import net.minecraft.entity.monster.EntityGolem
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.monster.EntitySlime
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.passive.EntityBat
import net.minecraft.entity.passive.EntitySquid
import net.minecraft.entity.passive.EntityVillager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.EnumAction
import net.minecraft.potion.Potion
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.Vec3
import javax.vecmath.Vector3d
import kotlin.math.*

/**
 * Checks if the entity is your friend
 */
fun Entity.isFriend() = this is EntityPlayer && FriendManager.isFriend(name)

/**
 * Checks if the entity is an animal
 */
fun Entity.isAnimal(): Boolean {
    return this is EntityAnimal || this is EntitySquid || this is EntityGolem || this is EntityBat
}

/**
 * Checks if the entity is a mob
 */
fun Entity.isMob(): Boolean {
    return this is EntityMob || this is EntityVillager || this is EntitySlime || this is EntityGhast || this is EntityDragon
}

/**
 * Checks if the player moves
 */
val EntityPlayerSP.moving
    get() = movementInput.moveForward != 0f || movementInput.moveStrafe != 0f

/**
 * Returns the minimum fall distance to take damage
 */
val EntityPlayerSP.minFallDistance
    get() = if (isPotionActive(Potion.jump)) 3f + getActivePotionEffect(Potion.jump).amplifier + 1 else 3f

/**
 * Returns the height of the player's jump
 */
val EntityPlayerSP.jumpHeight
    get() = 0.42f + if (isPotionActive(Potion.jump)) (getActivePotionEffect(Potion.jump).amplifier + 1) * 0.1f else 0f

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

fun EntityPlayerSP.strafe(speed: Double = this.speed, yaw: Float = directionYaw) {
    if (moving) {
        val angle = yaw.toDouble().toRadians()

        motionX = -sin(angle) * speed
        motionZ = cos(angle) * speed
    } else {
        motionX = 0.0
        motionZ = 0.0
    }
}

/**
 * Returns the player's ping
 */
val EntityPlayer.ping
    get() = mc.netHandler.getPlayerInfo(uniqueID)?.responseTime ?: 0

/**
 * Checks if the player eats
 */
val EntityPlayer.eating
    get() = itemInUse?.itemUseAction == EnumAction.EAT

/**
 * Checks if the player drinks
 */
val EntityPlayer.drinking
    get() = itemInUse?.itemUseAction == EnumAction.DRINK

/**
 * Checks if the player is aiming with a bow
 */
val EntityPlayer.aiming
    get() = itemInUse?.itemUseAction == EnumAction.BOW

/**
 * Returns the motion speed of the entity
 */
val Entity.speed
    get() = sqrt(motionX * motionX + motionZ * motionZ)

/**
 * Allows to get the distance between the current entity and [entity] from the nearest corner of the bounding box
 */
fun Entity.getDistanceToEntityBox(entity: Entity): Double {
    val eyes = getPositionEyes(0f)
    val pos = getNearestPoint(eyes, entity.entityBoundingBox)
    val xDist = abs(pos.xCoord - eyes.xCoord)
    val yDist = abs(pos.yCoord - eyes.yCoord)
    val zDist = abs(pos.zCoord - eyes.zCoord)
    
    return sqrt(xDist.pow(2) + yDist.pow(2) + zDist.pow(2))
}

private fun getNearestPoint(eye: Vec3, box: AxisAlignedBB): Vec3 {
    val origin = doubleArrayOf(eye.xCoord, eye.yCoord, eye.zCoord)
    val destMins = doubleArrayOf(box.minX, box.minY, box.minZ)
    val destMaxs = doubleArrayOf(box.maxX, box.maxY, box.maxZ)
    
    for (i in 0..2)
        if (origin[i] > destMaxs[i])
            origin[i] = destMaxs[i]
        else if (origin[i] < destMins[i])
            origin[i] = destMins[i]
    
    return Vec3(origin[0], origin[1], origin[2])
}

/**
 * Moves the player forward by [x] blocks
 */
fun EntityPlayerSP.forward(x: Double) {
    val yaw = rotationYaw.toDouble().toRadians()

    setPosition(posX - sin(yaw) * x, posY, posZ + cos(yaw) * x)
}

/**
 * Moves player up by [x] blocks
 */
fun EntityPlayerSP.upwards(x: Double) = setPosition(posX, posY + x, posZ)

/**
 * Render entity position
 */
val Entity.renderPos: Vector3d
    get() {
        val x = GLUtils.interpolate(lastTickPosX, posX) - mc.renderManager.viewerPosX
        val y = GLUtils.interpolate(lastTickPosY, posY) - mc.renderManager.viewerPosY
        val z = GLUtils.interpolate(lastTickPosZ, posZ) - mc.renderManager.viewerPosZ

        return Vector3d(x, y, z)
    }

val Entity.renderBoundingBox: AxisAlignedBB
    get() {
        return this.entityBoundingBox
            .offset(-this.posX, -this.posY, -this.posZ)
            .offset(this.renderPos.x, this.renderPos.y, this.renderPos.z)
    }

/**
 * Gets render distance to [entity]
 */
fun Entity.renderDistanceTo(entity: Entity): Double {
    val fromPos = this.renderPos
    val toPos = entity.renderPos

    val x = fromPos.x - toPos.x
    val y = fromPos.y - toPos.y
    val z = fromPos.z - toPos.z

    return sqrt(x * x + y * y + z * z)
}