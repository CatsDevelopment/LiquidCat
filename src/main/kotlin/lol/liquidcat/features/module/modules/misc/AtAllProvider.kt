/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.misc

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.PacketEvent
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntValue
import lol.liquidcat.utils.timer.MSTimer
import lol.liquidcat.utils.timer.TimeUtils
import net.minecraft.network.play.client.C01PacketChatMessage
import java.util.concurrent.LinkedBlockingQueue

class AtAllProvider : Module("AtAllProvider", "Automatically mentions everyone on the server when using '@a' in your message.", ModuleCategory.MISC) {

    private val maxDelay: Int by object : IntValue("MaxDelay", 1000, 0..20000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minDelay
            if (i > newValue) set(i)
        }
    }

    private val minDelay: Int by object : IntValue("MinDelay", 500, 0..20000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxDelay
            if (i < newValue) set(i)
        }
    }

    private val retry by BoolValue("Retry", false)

    private val sendQueue = LinkedBlockingQueue<String>()
    private val retryQueue: MutableList<String> = ArrayList()
    private val msTimer = MSTimer()
    private var delay = TimeUtils.randomDelay(minDelay, maxDelay)

    override fun onDisable() {
        synchronized(sendQueue) { sendQueue.clear() }
        synchronized(retryQueue) { retryQueue.clear() }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!msTimer.hasTimePassed(delay)) return

        try {
            synchronized(sendQueue) {
                if (sendQueue.isEmpty()) {
                    if (!retry || retryQueue.isEmpty()) return else sendQueue.addAll(retryQueue)
                }
                mc.thePlayer.sendChatMessage(sendQueue.take())
                msTimer.reset()
                delay = TimeUtils.randomDelay(minDelay, maxDelay)
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is C01PacketChatMessage) {
            val message = event.packet.getMessage()
            if (message.contains("@a")) {
                synchronized(sendQueue) {
                    for (playerInfo in mc.netHandler.playerInfoMap) {
                        val playerName = playerInfo.gameProfile.name
                        if (playerName == mc.thePlayer.name) continue
                        sendQueue.add(message.replace("@a", playerName))
                    }
                    if (retry) {
                        synchronized(retryQueue) {
                            retryQueue.clear()
                            retryQueue.addAll(listOf(*sendQueue.toTypedArray()))
                        }
                    }
                }
                event.cancelEvent()
            }
        }
    }
}