/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.misc

import io.netty.buffer.Unpooled
import lol.liquidcat.LiquidCat
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Listenable
import lol.liquidcat.event.PacketEvent
import lol.liquidcat.utils.mc
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.client.C17PacketCustomPayload
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket

object AntiForge : Listenable {

    init {
        LiquidCat.eventManager.registerListener(this)
    }

    @JvmField
    var enabled = true
    @JvmField
    var blockFML = true
    @JvmField
    var blockProxyPacket = true
    @JvmField
    var blockPayloadPackets = true

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (enabled && !mc.isIntegratedServerRunning) {
            if (blockProxyPacket && packet is FMLProxyPacket) event.cancelEvent()

            if (blockPayloadPackets && packet is C17PacketCustomPayload) {
                if (!packet.channelName.startsWith("MC|"))
                    event.cancelEvent()
                else if (packet.channelName.equals("MC|Brand", ignoreCase = true)) {
                    packet.data = PacketBuffer(Unpooled.buffer()).writeString("vanilla")
                }
            }
        }
    }

    override fun handleEvents() = true
}