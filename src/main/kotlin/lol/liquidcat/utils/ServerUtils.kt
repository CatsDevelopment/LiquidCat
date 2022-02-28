/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils

import net.ccbluex.liquidbounce.ui.client.GuiMainMenu
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.client.multiplayer.GuiConnecting
import net.minecraft.client.multiplayer.ServerData

object ServerUtils {

    var serverData: ServerData? = null

    @JvmStatic
    fun connectToLastServer() {
        if (serverData != null)
            mc.displayGuiScreen(GuiConnecting(GuiMultiplayer(GuiMainMenu()), mc, serverData))
    }

    val remoteIp: String
        get() {
            var serverIp = "SinglePlayer"

            if (mc.theWorld.isRemote) {
                val serverData = mc.currentServerData

                if (serverData != null) serverIp = serverData.serverIP
            }

            return serverIp
        }
}