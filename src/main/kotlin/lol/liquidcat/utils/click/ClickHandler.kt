/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.click

import lol.liquidcat.LiquidCat
import lol.liquidcat.event.ClickWindowEvent
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Listenable
import lol.liquidcat.event.PacketEvent
import lol.liquidcat.utils.timer.MSTimer
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement

object ClickHandler : Listenable {

    init {
        LiquidCat.eventManager.registerListener(this)
    }

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