/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.player

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.ScreenEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import net.minecraft.client.gui.GuiGameOver

@ModuleInfo("AutoRespawn", "Automatically respawns you after dying.", ModuleCategory.PLAYER)
class AutoRespawn : Module() {

    @EventTarget
    fun onScreen(event: ScreenEvent) {
        if (event.guiScreen is GuiGameOver) mc.thePlayer.respawnPlayer()
    }
}