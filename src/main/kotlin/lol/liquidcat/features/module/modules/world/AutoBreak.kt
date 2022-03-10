/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.world

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.block.getBlock
import net.minecraft.client.settings.GameSettings
import net.minecraft.init.Blocks

class AutoBreak : Module("AutoBreak", "Automatically breaks the block you are looking at.", ModuleCategory.WORLD) {

    override fun onDisable() {
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindAttack))
            mc.gameSettings.keyBindAttack.pressed = false
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.gameSettings.keyBindAttack.pressed = mc.objectMouseOver?.blockPos?.getBlock() != Blocks.air
    }
}