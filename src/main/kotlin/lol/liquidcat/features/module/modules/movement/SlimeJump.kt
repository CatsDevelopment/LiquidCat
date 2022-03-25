/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.movement

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.JumpEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.block.getBlock
import lol.liquidcat.value.FloatValue
import net.minecraft.block.BlockSlime
import net.minecraft.util.BlockPos

object SlimeJump : Module("SlimeJump", "Allows you to to jump higher on slime blocks.", ModuleCategory.MOVEMENT) {

    private val height by FloatValue("Height", 2f, 1.01f..5f)

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (BlockPos(mc.thePlayer.positionVector).down().getBlock() is BlockSlime)
            event.motion *= height
    }
}