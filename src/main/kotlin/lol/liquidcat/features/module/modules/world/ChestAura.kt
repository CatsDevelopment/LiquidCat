/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.world

import lol.liquidcat.event.EventState
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.MotionEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.modules.player.Blink
import lol.liquidcat.utils.block.getCenterDistance
import lol.liquidcat.utils.block.getVec
import lol.liquidcat.utils.block.searchBlocks
import lol.liquidcat.utils.sendPacket
import lol.liquidcat.utils.timer.MSTimer
import lol.liquidcat.value.BlockValue
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntValue
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.minecraft.block.Block
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.Vec3

//TODO Rewrite

object ChestAura : Module("ChestAura", "Automatically opens chests around you.", ModuleCategory.WORLD) {

    private val range by FloatValue("Range", 5F, 1f..6f)
    private val delay by IntValue("Delay", 100, 50..200)
    private val throughWalls by BoolValue("ThroughWalls", true)
    private val visualSwing by BoolValue("VisualSwing", true)
    private val chest by BlockValue("Chest", Block.getIdFromBlock(Blocks.chest))
    private val rotations by BoolValue("Rotations", true)

    private var currentBlock: BlockPos? = null
    private val timer = MSTimer()

    val clickedBlocks = mutableListOf<BlockPos>()

    override fun onDisable() {
        clickedBlocks.clear()
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (Blink.state)
            return

        when (event.eventState) {
            EventState.PRE -> {
                if (mc.currentScreen is GuiContainer) timer.reset()

                val radius = range + 1

                val eyesPos = Vec3(mc.thePlayer.posX, mc.thePlayer.entityBoundingBox.minY + mc.thePlayer.getEyeHeight(),
                        mc.thePlayer.posZ)

                currentBlock = searchBlocks(radius.toInt())
                        .filter {
                            Block.getIdFromBlock(it.value) == chest && !clickedBlocks.contains(it.key)
                                    && it.key.getCenterDistance() < range
                        }
                        .filter {
                            if (throughWalls)
                                return@filter true

                            val blockPos = it.key
                            val movingObjectPosition = mc.theWorld.rayTraceBlocks(eyesPos,
                                    blockPos.getVec(), false, true, false)

                            movingObjectPosition != null && movingObjectPosition.blockPos == blockPos
                        }
                        .minByOrNull { it.key.getCenterDistance() }?.key

                if (rotations)
                    RotationUtils.setTargetRotation((RotationUtils.faceBlock(currentBlock ?: return)
                            ?: return).rotation)
            }

            EventState.POST -> if (currentBlock != null && timer.hasTimePassed(delay.toLong())) {
                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem, currentBlock,
                                EnumFacing.DOWN, currentBlock!!.getVec())) {
                    if (visualSwing)
                        mc.thePlayer.swingItem()
                    else
                        sendPacket(C0APacketAnimation())

                    clickedBlocks.add(currentBlock!!)
                    currentBlock = null
                    timer.reset()
                }
            }
        }
    }
}