/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils

import net.minecraft.network.Packet
import net.minecraft.util.ChatComponentText

fun msg(message: String) {
    mc.ingameGUI.chatGUI.printChatMessage(ChatComponentText(message))
}

fun sendPacket(packet: Packet<*>) {
    mc.netHandler?.addToSendQueue(packet)
}