/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.block

import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.Vec3

class PlaceInfo(val blockPos: BlockPos, val enumFacing: EnumFacing,
                var vec3: Vec3 = Vec3(blockPos.x + 0.5, blockPos.y + 0.5, blockPos.z + 0.5)) {

    companion object {

        /**
         * Allows you to find a specific place info for your [blockPos]
         */
        @JvmStatic
        fun get(blockPos: BlockPos): PlaceInfo? {
            return when {
                blockPos.add(0, -1, 0).isClickable() ->
                    return PlaceInfo(blockPos.add(0, -1, 0), EnumFacing.UP)
                blockPos.add(0, 0, 1).isClickable() ->
                    return PlaceInfo(blockPos.add(0, 0, 1), EnumFacing.NORTH)
                blockPos.add(-1, 0, 0).isClickable() ->
                    return PlaceInfo(blockPos.add(-1, 0, 0), EnumFacing.EAST)
                blockPos.add(0, 0, -1).isClickable() ->
                    return PlaceInfo(blockPos.add(0, 0, -1), EnumFacing.SOUTH)
                blockPos.add(1, 0, 0).isClickable() ->
                    PlaceInfo(blockPos.add(1, 0, 0), EnumFacing.WEST)
                else -> null
            }
        }
    }
}