/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.misc

import lol.liquidcat.LiquidCat
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Listenable
import lol.liquidcat.event.PacketEvent
import lol.liquidcat.utils.mc
import net.minecraft.network.EnumConnectionState
import net.minecraft.network.handshake.client.C00Handshake
import java.text.MessageFormat
import kotlin.random.Random

object BungeeCordSpoof : Listenable {

    init {
        LiquidCat.eventManager.registerListener(this)
    }

    @JvmField
    var enabled = false

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C00Handshake && enabled && packet.requestedState == EnumConnectionState.LOGIN) {
            packet.ip = packet.ip + "\u0000" + MessageFormat.format("{0}.{1}.{2}.{3}",
                randomIpPart,
                randomIpPart,
                randomIpPart,
                randomIpPart
            ) + "\u0000" + mc.session.playerID.replace("-", "")
        }
    }

    private val randomIpPart: String
        get() = "${Random.nextInt(2)}${Random.nextInt(5)}${Random.nextInt(5)}"

    override fun handleEvents() = true
}