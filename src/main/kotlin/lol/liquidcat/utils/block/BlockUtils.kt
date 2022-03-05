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
import net.minecraft.util.MathHelper

object BlockUtils {

    /**
     * Get block name by [id]
     */
    @JvmStatic
    fun getBlockName(id: Int): String = Block.getBlockById(id).localizedName

    /**
     * Check if [axisAlignedBB] has collidable blocks using [predicate] check
     */
    fun collideBlock(axisAlignedBB: AxisAlignedBB, predicate: (Block?) -> Boolean): Boolean {
        for (x in MathHelper.floor_double(mc.thePlayer.entityBoundingBox.minX) until
                MathHelper.floor_double(mc.thePlayer.entityBoundingBox.maxX) + 1) {
            for (z in MathHelper.floor_double(mc.thePlayer.entityBoundingBox.minZ) until
                    MathHelper.floor_double(mc.thePlayer.entityBoundingBox.maxZ) + 1) {
                val block = BlockPos(x.toDouble(), axisAlignedBB.minY, z.toDouble()).getBlock()

                if (!predicate(block)) return false
            }
        }

        return true
    }

    /**
     * Check if [axisAlignedBB] has collidable blocks using [predicate] check
     */
    fun collideBlockIntersects(axisAlignedBB: AxisAlignedBB, predicate: (Block?) -> Boolean): Boolean {
        for (x in MathHelper.floor_double(mc.thePlayer.entityBoundingBox.minX) until
                MathHelper.floor_double(mc.thePlayer.entityBoundingBox.maxX) + 1) {
            for (z in MathHelper.floor_double(mc.thePlayer.entityBoundingBox.minZ) until
                    MathHelper.floor_double(mc.thePlayer.entityBoundingBox.maxZ) + 1) {
                val blockPos = BlockPos(x.toDouble(), axisAlignedBB.minY, z.toDouble())
                val block = blockPos.getBlock()

                if (predicate(block)) {
                    val boundingBox = block?.getCollisionBoundingBox(mc.theWorld, blockPos, blockPos.getState())
                        ?: continue

                    if (mc.thePlayer.entityBoundingBox.intersectsWith(boundingBox))
                        return true
                }
            }
        }
        return false
    }
}