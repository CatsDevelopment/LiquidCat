/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.movement

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.PacketEvent
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.value.BoolValue
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.settings.GameSettings
import net.minecraft.network.play.client.C16PacketClientStatus

object GuiMove : Module("GuiMove", "Allows you to walk while an inventory is opened.", ModuleCategory.MOVEMENT) {

    private val spoof by BoolValue("Spoof", true)

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

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        // Cancels the packet to make the server think that the player's inventory wasn't opened
        if (spoof && packet is C16PacketClientStatus && packet.status == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT)
            event.cancelEvent()
    }

    override fun onDisable() {
        if (mc.currentScreen != null)
            keys.forEach { key -> if (GameSettings.isKeyDown(key)) key.pressed = false }
    }
}