/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.render

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Render3DEvent
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.block.getBlock
import lol.liquidcat.utils.block.getBlockName
import lol.liquidcat.utils.render.GLUtils.drawBlockBox
import lol.liquidcat.utils.timer.MSTimer
import lol.liquidcat.value.BlockValue
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntValue
import net.ccbluex.liquidbounce.utils.render.ColorUtils.rainbow
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import java.awt.Color

object BlockESP : Module("BlockESP", "Allows you to see a selected block through walls.", ModuleCategory.RENDER) {

    private val block by BlockValue("Block", 168)
    private val radius by IntValue("Radius", 40, 5..120)
    private val red by IntValue("R", 255, 0..255)
    private val green by IntValue("G", 179, 0..255)
    private val blue by IntValue("B", 72, 0..255)
    private val rainbow by BoolValue("Rainbow", false)

    private val searchTimer = MSTimer()
    private val posList: MutableList<BlockPos> = ArrayList()
    private var thread: Thread? = null

    override val tag: String
        get() = getBlockName(block)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (searchTimer.hasTimePassed(1000L) && (thread == null || !thread!!.isAlive)) {
            val radius = radius
            val selectedBlock = Block.getBlockById(block)

            if (selectedBlock == null || selectedBlock == Blocks.air) return

            thread = Thread({
                val blockList = mutableListOf<BlockPos>()

                for (x in -radius until radius) {
                    for (y in radius downTo -radius + 1) {
                        for (z in -radius until radius) {
                            val xPos = mc.thePlayer.posX.toInt() + x
                            val yPos = mc.thePlayer.posY.toInt() + y
                            val zPos = mc.thePlayer.posZ.toInt() + z
                            val blockPos = BlockPos(xPos, yPos, zPos)
                            val block = blockPos.getBlock()

                            if (block == selectedBlock) blockList.add(blockPos)
                        }
                    }
                }

                searchTimer.reset()
                synchronized(posList) {
                    posList.clear()
                    posList.addAll(blockList)
                }
            }, "BlockESP-BlockFinder")

            thread!!.start()
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        synchronized(posList) {
            val color = if (rainbow) rainbow() else Color(red, green, blue)

            for (blockPos in posList) {
                drawBlockBox(blockPos, color, false, true)
            }
        }
    }
}