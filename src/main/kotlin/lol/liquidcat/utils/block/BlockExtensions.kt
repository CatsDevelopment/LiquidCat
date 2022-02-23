/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.block

import lol.liquidcat.utils.mc
import net.minecraft.block.Block
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3

fun BlockPos.getState() = mc.theWorld?.getBlockState(this)

fun BlockPos.getBlock() = getState()?.block

fun BlockPos.getMaterial() = getBlock()?.material

fun BlockPos.isFullBlock() = getBlock()?.isFullBlock

fun BlockPos.isReplaceable() = getMaterial()?.isReplaceable ?: false

fun BlockPos.isClickable() = getBlock()
    ?.canCollideCheck(getState(), false) ?: false && mc.theWorld.worldBorder.contains(this)

fun BlockPos.getVec() = Vec3(x + 0.5, y + 0.5, z + 0.5)

fun getBlockName(id: Int): String = Block.getBlockById(id).localizedName

fun searchBlocks(radius: Int): Map<BlockPos, Block> {
    val blocks = mutableMapOf<BlockPos, Block>()

    for (x in radius downTo -radius + 1)
        for (y in radius downTo -radius + 1)
            for (z in radius downTo -radius + 1) {
                val blockPos = BlockPos(
                    mc.thePlayer.posX.toInt() + x,
                    mc.thePlayer.posY.toInt() + y,
                    mc.thePlayer.posZ.toInt() + z
                )
                val block = blockPos.getBlock() ?: continue

                blocks[blockPos] = block
            }

    return blocks
}