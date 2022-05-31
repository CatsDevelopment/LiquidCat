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
import lol.liquidcat.utils.sendPacket
import net.minecraft.network.play.client.C19PacketResourcePackStatus
import net.minecraft.network.play.server.S48PacketResourcePackSend

object ResourcePackSpoof : Module("ResourcePackSpoof","Prevents servers from forcing you to download their resource pack.", ModuleCategory.MISC) {

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is S48PacketResourcePackSend) {

            sendPacket(C19PacketResourcePackStatus(packet.hash, C19PacketResourcePackStatus.Action.ACCEPTED))
            sendPacket(C19PacketResourcePackStatus(packet.hash, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED))

            event.cancelEvent()
        }

        if (packet is C19PacketResourcePackStatus &&
            (packet.status == C19PacketResourcePackStatus.Action.DECLINED ||
                    packet.status == C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD))
            event.cancelEvent()
    }
}