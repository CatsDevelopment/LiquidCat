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
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.utils.entity.minFallDistance
import lol.liquidcat.value.ListValue
import net.minecraft.network.play.client.C03PacketPlayer

@ModuleInfo(name = "NoFall", description = "Prevents you from taking fall damage.", category = ModuleCategory.PLAYER)
class NoFall : Module() {
    val modeValue = ListValue("Mode", arrayOf("Spoof"), "Spoof")

    override val tag: String
        get() = modeValue.get()

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C03PacketPlayer) {
            if (modeValue.get() == "Spoof" && mc.thePlayer.fallDistance > mc.thePlayer.minFallDistance) {
                packet.onGround = true
            }
        }
    }
}