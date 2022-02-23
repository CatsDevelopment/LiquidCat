/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.player

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.utils.block.getBlock
import net.minecraft.client.settings.GameSettings
import net.minecraft.init.Blocks

@ModuleInfo("Eagle", "Makes you eagle (aka. FastBridge).", ModuleCategory.PLAYER)
class Eagle : Module() {

    override fun onDisable() {
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            mc.gameSettings.keyBindSneak.pressed = false
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.gameSettings.keyBindSneak.pressed = mc.thePlayer.position.down().getBlock() == Blocks.air
    }
}