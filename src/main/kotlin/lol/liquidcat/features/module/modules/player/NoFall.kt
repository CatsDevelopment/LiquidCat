/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.player

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.PacketEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.entity.minFallDistance
import lol.liquidcat.value.ListValue
import net.minecraft.network.play.client.C03PacketPlayer

//TODO Add more modes

class NoFall : Module("NoFall", "Prevents you from taking fall damage.", ModuleCategory.PLAYER) {

    val modeValue = ListValue("Mode", arrayOf("Spoof", "NoGround"), "Spoof")

    override val tag: String
        get() = modeValue.get()

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C03PacketPlayer)
            when {
                modeValue.get() == "Spoof" && mc.thePlayer.fallDistance > mc.thePlayer.minFallDistance -> {
                    packet.onGround = true
                    mc.thePlayer.fallDistance = 0.0f
                }
                modeValue.get() == "NoGround" -> packet.onGround = false
            }
    }
}