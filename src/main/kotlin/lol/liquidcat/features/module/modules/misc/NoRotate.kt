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
import net.minecraft.network.play.server.S08PacketPlayerPosLook

class NoRotate : Module("NoRotate", "Prevents the server from rotating your head.", ModuleCategory.MISC) {

    @EventTarget
    fun onPacket(event: PacketEvent) {
        mc.thePlayer ?: return

        val packet = event.packet

        if (packet is S08PacketPlayerPosLook) {
            packet.yaw = mc.thePlayer.rotationYaw
            packet.pitch = mc.thePlayer.rotationPitch
        }
    }
}