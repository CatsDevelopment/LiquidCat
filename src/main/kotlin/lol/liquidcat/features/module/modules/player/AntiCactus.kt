/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.player

import lol.liquidcat.event.BlockBBEvent
import lol.liquidcat.event.EventTarget
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import net.minecraft.block.BlockCactus
import net.minecraft.util.AxisAlignedBB

class AntiCactus : Module("AntiCactus", "Prevents cactuses from damaging you.", ModuleCategory.PLAYER) {

    @EventTarget
    fun onBlockBB(event: BlockBBEvent) {
        if (event.block is BlockCactus)
            event.boundingBox = AxisAlignedBB(event.x.toDouble(), event.y.toDouble(), event.z.toDouble(), event.x + 1.0, event.y + 1.0, event.z + 1.0)
    }
}