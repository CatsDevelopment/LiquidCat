/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.movement

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.JumpEvent
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.block.collideBlock
import lol.liquidcat.utils.block.down
import lol.liquidcat.value.FloatValue
import net.minecraft.block.BlockLiquid

object ReverseStep : Module("ReverseStep", "Allows you to step down blocks faster.", ModuleCategory.MOVEMENT) {

    private val motion by FloatValue("Motion", 1f, 0.21f..1f)

    private var jumped = false

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.onGround) jumped = false
        if (mc.thePlayer.motionY > 0) jumped = true

        if (collideBlock(mc.thePlayer.entityBoundingBox) { it is BlockLiquid } || collideBlock(mc.thePlayer.entityBoundingBox.down(0.01)) { it is BlockLiquid }) return

        if (!mc.gameSettings.keyBindJump.isKeyDown && !mc.thePlayer.onGround && !mc.thePlayer.movementInput.jump && mc.thePlayer.motionY <= 0.0 && mc.thePlayer.fallDistance <= 1f && !jumped) mc.thePlayer.motionY =
            (-motion).toDouble()
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        jumped = true
    }
}