/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils

import lol.liquidcat.event.ClickWindowEvent
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Listenable
import lol.liquidcat.event.PacketEvent
import lol.liquidcat.utils.timer.MSTimer
import lol.liquidcat.value.IntValue
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement

object ClickHandler : Listenable {

    val a = IntValue("", 1, 3..5)

    val CLICK_TIMER = MSTimer()

    @EventTarget
    fun onClick(event: ClickWindowEvent) {
        CLICK_TIMER.reset()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is C08PacketPlayerBlockPlacement) CLICK_TIMER.reset()
    }

    override fun handleEvents() = true
}