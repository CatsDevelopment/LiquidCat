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
import lol.liquidcat.features.module.modules.player.AutoTool
import lol.liquidcat.features.module.modules.player.Blink
import lol.liquidcat.utils.block.getBlock
import lol.liquidcat.utils.block.getCenterDistance
import lol.liquidcat.utils.block.searchBlocks
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.utils.sendPacket
import lol.liquidcat.utils.timer.MSTimer
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntValue
import lol.liquidcat.value.ListValue
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.block.BlockLiquid
import net.minecraft.init.Blocks
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.Vec3
import java.awt.Color
import kotlin.math.roundToInt

//TODO Rewrite

object Nuker : Module("Nuker", "Breaks all blocks around you.", ModuleCategory.WORLD) {

    private val radius by FloatValue("Radius", 5.2f, 1F..6f)
    private val throughWalls by BoolValue("ThroughWalls", false)
    private val priority by ListValue("Priority", arrayOf("Distance", "Hardness"), "Distance")
    private val rotations by BoolValue("Rotations", true)
    private val layer by BoolValue("Layer", false)
    private val hitDelay by IntValue("HitDelay", 4, 0..20)
    private val nukeValue by IntValue("Nuke", 1, 1..20)
    private val nukeDelay by IntValue("NukeDelay", 1, 100..5000)

    private val attackedBlocks = arrayListOf<BlockPos>()
    var currentDamage = 0f
    private var currentBlock: BlockPos? = null
    private var blockHitDelay = 0
    private var nukeTimer = MSTimer()
    private var nuke = 0

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        // Block hit delay
        if (blockHitDelay > 0 && !Blink.state) {
            blockHitDelay--
            return
        }

        // Reset bps
        if (nukeTimer.hasTimePassed(nukeDelay.toLong())) {
            nuke = 0
            nukeTimer.reset()
        }

        // Clear blocks
        attackedBlocks.clear()

        if (mc.playerController.isNotCreative) {
            // Default nuker

            val validBlocks = searchBlocks(radius.roundToInt() + 1)
                    .filter { (pos, block) ->
                        if (pos.getCenterDistance() <= radius && validBlock(block)) {
                            if (layer && pos.y < mc.thePlayer.posY) { // Layer: Break all blocks above you
                                return@filter false
                            }

                            if (!throughWalls) { // ThroughWalls: Just break blocks in your sight
                                // Raytrace player eyes to block position (through walls check)
                                val eyesPos = Vec3(mc.thePlayer.posX, mc.thePlayer.entityBoundingBox.minY +
                                        mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ)
                                val blockVec = Vec3(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
                                val rayTrace = mc.theWorld.rayTraceBlocks(eyesPos, blockVec,
                                        false, true, false)

                                // Check if block is visible
                                rayTrace != null && rayTrace.blockPos == pos
                            }else true // Done
                        }else false // Bad block
                    }.toMutableMap()

            do {
                val (blockPos, block) = when(priority) {
                    "Distance" -> validBlocks.minByOrNull { (pos, block) ->
                        val distance = pos.getCenterDistance()
                        val safePos = BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)

                        if (pos.x == safePos.x && safePos.y <= pos.y && pos.z == safePos.z)
                            Double.MAX_VALUE - distance // Last block
                        else
                            distance
                    }
                    "Hardness" -> validBlocks.maxByOrNull { (pos, block) ->
                        val hardness = block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, pos).toDouble()

                        val safePos = BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)
                        if (pos.x == safePos.x && safePos.y <= pos.y && pos.z == safePos.z)
                            Double.MIN_VALUE + hardness // Last block
                        else
                            hardness
                    }
                    else -> return // what? why?
                } ?: return // well I guess there is no block to break :(

                // Reset current damage in case of block switch
                if (blockPos != currentBlock)
                    currentDamage = 0F

                // Change head rotations to next block
                if (rotations) {
                    val rotation = RotationUtils.faceBlock(blockPos) ?: return // In case of a mistake. Prevent flag.
                    RotationUtils.setTargetRotation(rotation.rotation)
                }

                // Set next target block
                currentBlock = blockPos
                attackedBlocks.add(blockPos)

                // Call auto tool
                if (AutoTool.state)
                    AutoTool.switchSlot(blockPos)

                // Start block breaking
                if (currentDamage == 0F) {
                    sendPacket(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
                            blockPos, EnumFacing.DOWN))

                    // End block break if able to break instant
                    if (block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, blockPos) >= 1F) {
                        currentDamage = 0F
                        mc.thePlayer.swingItem()
                        mc.playerController.onPlayerDestroyBlock(blockPos, EnumFacing.DOWN)
                        blockHitDelay = hitDelay
                        validBlocks -= blockPos
                        nuke++
                        continue // Next break
                    }
                }

                // Break block
                mc.thePlayer.swingItem()
                currentDamage += block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, blockPos)
                mc.theWorld.sendBlockBreakProgress(mc.thePlayer.entityId, blockPos, (currentDamage * 10F).toInt() - 1)

                // End of breaking block
                if (currentDamage >= 1F) {
                    sendPacket(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN))
                    mc.playerController.onPlayerDestroyBlock(blockPos, EnumFacing.DOWN)
                    blockHitDelay = hitDelay
                    currentDamage = 0F
                }
                return // Break out
            } while (nuke < nukeValue)
        } else {
            // Fast creative mode nuker (CreativeStorm option)

            // Unable to break with swords in creative mode
            if (mc.thePlayer.heldItem?.item is ItemSword)
                return

            // Serach for new blocks to break
            searchBlocks(radius.roundToInt() + 1)
                    .filter { (pos, block) ->
                        if (pos.getCenterDistance() <= radius && validBlock(block)) {
                            if (layer && pos.y < mc.thePlayer.posY) { // Layer: Break all blocks above you
                                return@filter false
                            }

                            if (!throughWalls) { // ThroughWalls: Just break blocks in your sight
                                // Raytrace player eyes to block position (through walls check)
                                val eyesPos = Vec3(mc.thePlayer.posX, mc.thePlayer.entityBoundingBox.minY +
                                        mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ)
                                val blockVec = Vec3(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
                                val rayTrace = mc.theWorld.rayTraceBlocks(eyesPos, blockVec,
                                        false, true, false)

                                // Check if block is visible
                                rayTrace != null && rayTrace.blockPos == pos
                            }else true // Done
                        }else false // Bad block
                    }
                    .forEach { (pos, block) ->
                        // Instant break block
                        sendPacket(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
                                pos, EnumFacing.DOWN))
                        mc.thePlayer.swingItem()
                        sendPacket(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                                pos, EnumFacing.DOWN))
                        attackedBlocks.add(pos)
                    }
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        // Safe block
        if (!layer) {
            val safePos = BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)
            val safeBlock = safePos.getBlock()
            if (safeBlock != null && validBlock(safeBlock))
                GLUtils.drawBlockBox(safePos, Color.GREEN, false, true)
        }

        // Just draw all blocks
        for (blockPos in attackedBlocks)
            GLUtils.drawBlockBox(blockPos, Color.RED, false, true)
    }

    /**
     * Check if [block] is a valid block to break
     */
    private fun validBlock(block: Block) = block !is BlockAir && block !is BlockLiquid && block != Blocks.bedrock
}