/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.movement

import lol.liquidcat.event.*
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.entity.directionYaw
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.ListValue
import net.minecraft.block.BlockAir
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.AxisAlignedBB
import kotlin.math.cos
import kotlin.math.sin

object Spider : Module("Spider", "Allows you to climb up walls like a spider.", ModuleCategory.MOVEMENT) {

    private val mode by ListValue("Mode", arrayOf("Simple", "Clip"), "Simple")
    private val motion by FloatValue("Motion", 0f, 0f..1f)

    private var glitch = false

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (mode == "Simple")
            if (mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isOnLadder && !mc.thePlayer.isInWater && !mc.thePlayer.isInLava) {
                event.y = motion.toDouble()
                mc.thePlayer.motionY = 0.0
            }
    }

    @EventTarget
    fun onUpdate(event: MotionEvent) {
        if (event.eventState == EventState.POST && mode == "Clip") {
            if (mc.thePlayer.motionY < 0) glitch = true
            if (mc.thePlayer.isCollidedHorizontally && mc.thePlayer.onGround) mc.thePlayer.jump()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C03PacketPlayer)
            if (glitch) {
                val yaw = mc.thePlayer.directionYaw

                packet.x = packet.x - sin(yaw) * 0.00000001
                packet.z = packet.z + cos(yaw) * 0.00000001

                glitch = false
            }
    }

    @EventTarget
    fun onBlockBB(event: BlockBBEvent) {
        mc.thePlayer ?: return

        if (mode == "Clip")
            if (event.block is BlockAir && event.y < mc.thePlayer.posY && mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isOnLadder && !mc.thePlayer.isInWater && !mc.thePlayer.isInLava)
                event.boundingBox = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
                    .offset(mc.thePlayer.posX, (mc.thePlayer.posY.toInt() - 1).toDouble(), mc.thePlayer.posZ)
    }
}