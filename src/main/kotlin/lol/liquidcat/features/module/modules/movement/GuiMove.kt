/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.movement

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.settings.GameSettings

@ModuleInfo("GuiMove", "Allows you to walk while an inventory is opened.", ModuleCategory.MOVEMENT)
class GuiMove : Module() {

    private val keys = arrayOf(
        mc.gameSettings.keyBindForward,
        mc.gameSettings.keyBindBack,
        mc.gameSettings.keyBindRight,
        mc.gameSettings.keyBindLeft,
        mc.gameSettings.keyBindJump,
        mc.gameSettings.keyBindSprint
    )

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.currentScreen !is GuiChat)
            keys.forEach { key -> key.pressed = GameSettings.isKeyDown(key) }
    }

    override fun onDisable() {
        if (mc.currentScreen != null)
            keys.forEach { key -> if (GameSettings.isKeyDown(key)) key.pressed = false }
    }
}