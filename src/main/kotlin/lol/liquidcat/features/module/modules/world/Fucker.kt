/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.world

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Render3DEvent
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.modules.combat.KillAura
import lol.liquidcat.features.module.modules.player.AutoTool
import lol.liquidcat.utils.block.*
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.utils.sendPacket
import lol.liquidcat.utils.timer.MSTimer
import lol.liquidcat.value.*
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.Vec3
import java.awt.Color

//TODO Rewrite

object Fucker : Module("Fucker", "Destroys selected blocks around you. (aka.  IDNuker)", ModuleCategory.WORLD) {

    private val block by BlockValue("Block", 26)
    private val throughWalls by ListValue("ThroughWalls", arrayOf("None", "Raycast", "Around"), "None")
    private val range by FloatValue("Range", 5f, 1f..7f)
    private val action by ListValue("Action", arrayOf("Destroy", "Use"), "Destroy")
    private val instant by BoolValue("Instant", false)
    private val switch by IntValue("SwitchDelay", 250, 0..1000)
    private val swing by BoolValue("Swing", true)
    private val rotations by BoolValue("Rotations", true)
    private val surroundings by BoolValue("Surroundings", true)
    private val noHit by BoolValue("NoHit", false)

    private var pos: BlockPos? = null
    private var oldPos: BlockPos? = null
    private var blockHitDelay = 0
    private val switchTimer = MSTimer()
    var currentDamage = 0F

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (noHit) {
            if (KillAura.state && KillAura.target != null)
                return
        }

        val targetId = block

        if (pos == null || Block.getIdFromBlock(pos?.getBlock()) != targetId ||
                pos!!.getCenterDistance() > range)
            pos = find(targetId)

        // Reset current breaking when there is no target block
        if (pos == null) {
            currentDamage = 0F
            return
        }

        var currentPos = pos ?: return
        var rotations = RotationUtils.faceBlock(currentPos) ?: return

        // Surroundings
        var surroundings = false

        if (this.surroundings) {
            val eyes = mc.thePlayer.getPositionEyes(1F)
            val blockPos = mc.theWorld.rayTraceBlocks(eyes, rotations.vec, false,
                    false, true).blockPos

            if (blockPos != null && blockPos.getBlock() !is BlockAir) {
                if (currentPos.x != blockPos.x || currentPos.y != blockPos.y || currentPos.z != blockPos.z)
                    surroundings = true

                pos = blockPos
                currentPos = pos ?: return
                rotations = RotationUtils.faceBlock(currentPos) ?: return
            }
        }

        // Reset switch timer when position changed
        if (oldPos != null && oldPos != currentPos) {
            currentDamage = 0F
            switchTimer.reset()
        }

        oldPos = currentPos

        if (!switchTimer.hasTimePassed(switch.toLong()))
            return

        // Block hit delay
        if (blockHitDelay > 0) {
            blockHitDelay--
            return
        }

        // Face block
        if (this.rotations)
            RotationUtils.setTargetRotation(rotations.rotation)

        when {
            // Destory block
            action.equals("destroy", true) || surroundings -> {
                // Auto Tool
                if (AutoTool.state) AutoTool.switchSlot(currentPos)

                // Break block
                if (instant) {
                    // CivBreak style block breaking
                    sendPacket(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
                            currentPos, EnumFacing.DOWN))

                    if (swing)
                        mc.thePlayer.swingItem()

                    sendPacket(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                            currentPos, EnumFacing.DOWN))
                    currentDamage = 0F
                    return
                }

                // Minecraft block breaking
                val block = currentPos.getBlock() ?: return

                if (currentDamage == 0F) {
                    sendPacket(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
                            currentPos, EnumFacing.DOWN))

                    if (mc.thePlayer.capabilities.isCreativeMode ||
                            block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, pos) >= 1.0F) {
                        if (swing)
                            mc.thePlayer.swingItem()
                        mc.playerController.onPlayerDestroyBlock(pos, EnumFacing.DOWN)

                        currentDamage = 0F
                        pos = null
                        return
                    }
                }

                if (swing)
                    mc.thePlayer.swingItem()

                currentDamage += block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, currentPos)
                mc.theWorld.sendBlockBreakProgress(mc.thePlayer.entityId, currentPos, (currentDamage * 10F).toInt() - 1)

                if (currentDamage >= 1F) {
                    sendPacket(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                            currentPos, EnumFacing.DOWN))
                    mc.playerController.onPlayerDestroyBlock(currentPos, EnumFacing.DOWN)
                    blockHitDelay = 4
                    currentDamage = 0F
                    pos = null
                }
            }

            // Use block
            action.equals("use", true) -> if (mc.playerController.onPlayerRightClick(
                            mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem, pos, EnumFacing.DOWN,
                            Vec3(currentPos.x.toDouble(), currentPos.y.toDouble(), currentPos.z.toDouble()))) {
                if (swing)
                    mc.thePlayer.swingItem()

                blockHitDelay = 4
                currentDamage = 0F
                pos = null
            }
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        GLUtils.drawBlockBox(pos ?: return, Color.RED, false, true)
    }

    /**
     * Find new target block by [targetID]
     */
    private fun find(targetID: Int) = searchBlocks(range.toInt() + 1)
            .filter {
                Block.getIdFromBlock(it.value) == targetID && it.key.getCenterDistance() <= range
                        && (isHitable(it.key) || surroundings)
            }
            .minByOrNull { it.key.getCenterDistance() }?.key

    /**
     * Check if block is hitable (or allowed to hit through walls)
     */
    private fun isHitable(blockPos: BlockPos): Boolean {
        return when (throughWalls.lowercase()) {
            "raycast" -> {
                val eyesPos = Vec3(mc.thePlayer.posX, mc.thePlayer.entityBoundingBox.minY +
                        mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ)
                val movingObjectPosition = mc.theWorld.rayTraceBlocks(eyesPos,
                        Vec3(blockPos.x + 0.5, blockPos.y + 0.5, blockPos.z + 0.5), false,
                        true, false)

                movingObjectPosition != null && movingObjectPosition.blockPos == blockPos
            }
            "around" -> !blockPos.down().isFullBlock()!! || !blockPos.up().isFullBlock()!! || !blockPos.north().isFullBlock()!!
                    || !blockPos.east().isFullBlock()!! || !blockPos.south().isFullBlock()!! || !blockPos.west().isFullBlock()!!
            else -> true
        }
    }

    override val tag: String
        get() = getBlockName(block)
}