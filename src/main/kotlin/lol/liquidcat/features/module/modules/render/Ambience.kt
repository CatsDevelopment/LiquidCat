/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.render

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.PacketEvent
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.value.IntValue
import lol.liquidcat.value.ListValue
import net.minecraft.network.play.server.S03PacketTimeUpdate

object Ambience : Module("Ambience", "Changes the world time", ModuleCategory.RENDER) {

    val mode by ListValue("Mode", arrayOf("Custom", "Day", "Night", "Noon", "Midnight", "Sunrise", "Sunset"), "Sunset")
    val time by IntValue("Time", 24000, 0..24000)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {

        // Changes the world time
        mc.theWorld.worldTime = when (mode) {
            "Day" -> 1000
            "Night" -> 13000
            "Noon" -> 6000
            "Midnight" -> 18000
            "Sunrise" -> 23000
            "Sunset" -> 12000

            else -> time.toLong()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {

        // Cancels the packet that synchronizes the world time
        if (event.packet is S03PacketTimeUpdate)
            event.cancelEvent()
    }
}