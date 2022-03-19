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
        try {
            fastRenderField = GameSettings::class.java.getDeclaredField("ofFastRender")

            if (!fastRenderField!!.isAccessible)
                fastRenderField!!.isAccessible = true

        } catch (ignored: NoSuchFieldException) {
        }
    }

    @JvmStatic
    fun disableFastRender() {
        try {
            if (fastRenderField != null) {
                if (!fastRenderField!!.isAccessible)
                    fastRenderField!!.isAccessible = true

                fastRenderField!!.setBoolean(mc.gameSettings, false)
            }
        } catch (ignored: IllegalAccessException) {
        }
    }
}

val mc: Minecraft = Minecraft.getMinecraft()

fun msg(message: String) {
    mc.ingameGUI.chatGUI.printChatMessage(ChatComponentText(message))
}

fun sendPacket(packet: Packet<*>) {
    mc.netHandler?.addToSendQueue(packet)
}