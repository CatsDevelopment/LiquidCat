/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.world

import lol.liquidcat.LiquidCat
import lol.liquidcat.event.EventState
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.MotionEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.player.Blink
import net.ccbluex.liquidbounce.utils.RotationUtils
import lol.liquidcat.utils.block.BlockUtils
import lol.liquidcat.utils.block.getVec
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import lol.liquidcat.value.BlockValue
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntegerValue
import net.minecraft.block.Block
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.Vec3

//TODO Rewrite

@ModuleInfo("ChestAura", "Automatically opens chests around you.", ModuleCategory.WORLD)
object ChestAura : Module() {

    private val rangeValue = FloatValue("Range", 5F, 1F, 6F)
    private val delayValue = IntegerValue("Delay", 100, 50, 200)
    private val throughWallsValue = BoolValue("ThroughWalls", true)
    private val visualSwing = BoolValue("VisualSwing", true)
    private val chestValue = BlockValue("Chest", Block.getIdFromBlock(Blocks.chest))
    private val rotationsValue = BoolValue("Rotations", true)

    private var currentBlock: BlockPos? = null
    private val timer = MSTimer()

    val clickedBlocks = mutableListOf<BlockPos>()

    override fun onDisable() {
        clickedBlocks.clear()
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (LiquidCat.moduleManager[Blink::class.java]!!.state)
            return

        when (event.eventState) {
            EventState.PRE -> {
                if (mc.currentScreen is GuiContainer) timer.reset()

                val radius = rangeValue.get() + 1

                val eyesPos = Vec3(mc.thePlayer.posX, mc.thePlayer.entityBoundingBox.minY + mc.thePlayer.getEyeHeight(),
                        mc.thePlayer.posZ)

                currentBlock = BlockUtils.searchBlocks(radius.toInt())
                        .filter {
                            Block.getIdFromBlock(it.value) == chestValue.get() && !clickedBlocks.contains(it.key)
                                    && BlockUtils.getCenterDistance(it.key) < rangeValue.get()
                        }
                        .filter {
                            if (throughWallsValue.get())
                                return@filter true

                            val blockPos = it.key
                            val movingObjectPosition = mc.theWorld.rayTraceBlocks(eyesPos,
                                    blockPos.getVec(), false, true, false)

                            movingObjectPosition != null && movingObjectPosition.blockPos == blockPos
                        }
                        .minBy { BlockUtils.getCenterDistance(it.key) }?.key

                if (rotationsValue.get())
                    RotationUtils.setTargetRotation((RotationUtils.faceBlock(currentBlock ?: return)
                            ?: return).rotation)
            }

            EventState.POST -> if (currentBlock != null && timer.hasTimePassed(delayValue.get().toLong())) {
                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem, currentBlock,
                                EnumFacing.DOWN, currentBlock!!.getVec())) {
                    if (visualSwing.get())
                        mc.thePlayer.swingItem()
                    else
                        mc.netHandler.addToSendQueue(C0APacketAnimation())

                    clickedBlocks.add(currentBlock!!)
                    currentBlock = null
                    timer.reset()
                }
            }
        }
    }
}