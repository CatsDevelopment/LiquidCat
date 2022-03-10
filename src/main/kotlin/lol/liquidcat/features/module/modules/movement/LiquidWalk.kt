/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.movement

import lol.liquidcat.event.*
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.block.BlockUtils.collideBlock
import lol.liquidcat.utils.block.getBlock
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.ListValue
import net.minecraft.block.BlockLiquid
import net.minecraft.block.material.Material
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos

//TODO Rewrite and add more modes

class LiquidWalk : Module("LiquidWalk", "Allows you to walk on water.", ModuleCategory.MOVEMENT) {

    val modeValue = ListValue("Mode", arrayOf("Vanilla", "NCP", "Dolphin"), "NCP")
    private val noJumpValue = BoolValue("NoJump", false)

    private var nextTick = false

    override val tag: String
        get() = modeValue.get()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!mc.thePlayer.isSneaking)
            when (modeValue.get()) {
                "NCP", "Vanilla" -> if (collideBlock(mc.thePlayer.entityBoundingBox) { it is BlockLiquid }
                    && mc.thePlayer.isInsideOfMaterial(Material.air) && !mc.thePlayer.isSneaking)
                    mc.thePlayer.motionY = 0.08

                "Dolphin" -> if (mc.thePlayer.isInWater) mc.thePlayer.motionY += 0.03999999910593033
            }
    }

    @EventTarget
    fun onBlockBB(event: BlockBBEvent) {
        if (mc.thePlayer == null || mc.thePlayer.entityBoundingBox == null) return

        if (event.block is BlockLiquid && !collideBlock(mc.thePlayer.entityBoundingBox) { it is BlockLiquid } && !mc.thePlayer.isSneaking)
            when (modeValue.get()) {
                "NCP", "Vanilla" -> event.boundingBox = AxisAlignedBB.fromBounds(
                    event.x.toDouble(),
                    event.y.toDouble(),
                    event.z.toDouble(),
                    (event.x + 1).toDouble(),
                    (event.y + 1).toDouble(),
                    (event.z + 1).toDouble()
                )
            }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.thePlayer == null || modeValue.get() != "NCP") return

        val aabb = AxisAlignedBB(
            mc.thePlayer.entityBoundingBox.maxX, mc.thePlayer.entityBoundingBox.maxY,
            mc.thePlayer.entityBoundingBox.maxZ, mc.thePlayer.entityBoundingBox.minX,
            mc.thePlayer.entityBoundingBox.minY - 0.01, mc.thePlayer.entityBoundingBox.minZ
        )

        if (event.packet is C03PacketPlayer) {
            if (collideBlock(aabb) { it is BlockLiquid }) {
                nextTick = !nextTick
                if (nextTick) event.packet.y -= 0.001
            }
        }
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (noJumpValue.get() && BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.01, mc.thePlayer.posZ).getBlock() is BlockLiquid)
            event.cancelEvent()
    }
}