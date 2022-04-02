/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.settings.GameSettings
import net.minecraft.network.Packet
import net.minecraft.util.ChatComponentText
import java.lang.reflect.Field

object ClientUtils {
    private var fastRenderField: Field? = null

    init {
        runCatching {
            fastRenderField = GameSettings::class.java.getDeclaredField("ofFastRender")

            if (!fastRenderField!!.isAccessible)
                fastRenderField!!.isAccessible = true
        }
    }

    /**
     * Disables OptiFine FastRender
     */
    @JvmStatic
    fun disableFastRender() {
        runCatching {
            if (fastRenderField != null) {
                if (!fastRenderField!!.isAccessible)
                    fastRenderField!!.isAccessible = true

                fastRenderField!!.setBoolean(mc.gameSettings, false)
            }
        }
    }
}

/**
 * Minecraft instance
 */
val mc = Minecraft.getMinecraft()!!

/**
 * Sends client-side [message] to chat
 */
fun msg(message: String) {
    mc.ingameGUI.chatGUI.printChatMessage(ChatComponentText(message))
}

/**
 * Sends a [packet] to the server
 */
fun sendPacket(packet: Packet<*>) = mc.netHandler?.addToSendQueue(packet)