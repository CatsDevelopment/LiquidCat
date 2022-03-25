/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.world

import lol.liquidcat.event.*
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.block.getBlock
import lol.liquidcat.utils.block.getCenterDistance
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.utils.sendPacket
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.minecraft.block.BlockAir
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import java.awt.Color

//TODO Rewrite

object CivBreak : Module("CivBreak", "Allows you to break blocks instantly.", ModuleCategory.WORLD) {

    private val range by FloatValue("Range", 5f, 1f..6f)
    private val rotations by BoolValue("Rotations", true)
    private val visualSwing by BoolValue("VisualSwing", true)
    private val airReset by BoolValue("Air-Reset", true)
    private val rangeReset by BoolValue("Range-Reset", true)
    
    private var blockPos: BlockPos? = null
    private var enumFacing: EnumFacing? = null

    @EventTarget
    fun onBlockClick(event: ClickBlockEvent) {
        if (event.clickedBlock?.getBlock() == Blocks.bedrock)
            return

        blockPos = event.clickedBlock
        enumFacing = event.enumFacing

        // Break
        sendPacket(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, enumFacing))
        sendPacket(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, enumFacing))
    }

    @EventTarget
    fun onUpdate(event: MotionEvent) {
        val pos = blockPos ?: return

        if (airReset && pos.getBlock() is BlockAir ||
                rangeReset && pos.getCenterDistance() > range) {
            blockPos = null
            return
        }

        if (pos.getBlock() is BlockAir || pos.getCenterDistance() > range)
            return

        when (event.eventState) {
            EventState.PRE -> if (rotations)
                RotationUtils.setTargetRotation((RotationUtils.faceBlock(pos) ?: return).rotation)

            EventState.POST -> {
                if (visualSwing)
                    mc.thePlayer.swingItem()
                else
                    sendPacket(C0APacketAnimation())

                // Break
                sendPacket(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, enumFacing))
                sendPacket(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, enumFacing))
                mc.playerController.clickBlock(blockPos, enumFacing)
            }
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        GLUtils.drawBlockBox(blockPos ?: return, Color.RED, false, true)
    }
}