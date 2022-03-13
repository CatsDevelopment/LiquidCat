/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.misc

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.PacketEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import net.minecraft.network.play.server.S2EPacketCloseWindow

class NoClose : Module("NoClose", "Prevents the server from closing your inventory.", ModuleCategory.MISC) {

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is S2EPacketCloseWindow) event.cancelEvent()
    }
}