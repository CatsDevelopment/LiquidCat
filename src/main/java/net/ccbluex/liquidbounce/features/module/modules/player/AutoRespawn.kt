/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import lol.liquidcat.LiquidCat
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.features.module.modules.exploit.Ghost
import lol.liquidcat.value.BoolValue
import net.minecraft.client.gui.GuiGameOver

@ModuleInfo(name = "AutoRespawn", description = "Automatically respawns you after dying.", category = ModuleCategory.PLAYER)
class AutoRespawn : Module() {

    private val instantValue = BoolValue("Instant", true)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (LiquidCat.moduleManager[Ghost::class.java]!!.state)
            return

        if (if (instantValue.get()) mc.thePlayer.health == 0F || mc.thePlayer.isDead else mc.currentScreen is GuiGameOver
                        && (mc.currentScreen as GuiGameOver).enableButtonsTimer >= 20) {
            mc.thePlayer.respawnPlayer()
            mc.displayGuiScreen(null)
        }
    }
}