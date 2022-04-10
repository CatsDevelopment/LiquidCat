/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.ui.client.guis

import lol.liquidcat.LiquidCat
import lol.liquidcat.utils.render.GLUtils
import net.ccbluex.liquidbounce.ui.client.GuiModsMenu
import lol.liquidcat.ui.client.ImageButton
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager
import net.minecraft.client.gui.*
import net.minecraft.util.ResourceLocation

class GuiMainMenu : GuiScreen(), GuiYesNoCallback {

    override fun initGui() {
        val dWidth = width / 2
        val dHeight = height - 40
        
        buttonList.add(ImageButton(0, "Singleplayer", "singleplayer.png", dWidth, dHeight, 32, 32))
        buttonList.add(ImageButton(1, "Multiplayer", "multiplayer.png", dWidth - 32 - 5, dHeight, 32, 32))
        buttonList.add(ImageButton(2, "Alt Manager", "altmanager.png", dWidth + 32 + 5, dHeight, 32, 32))
        buttonList.add(ImageButton(3, "Settings", "settings.png", dWidth - 64 - 10, dHeight, 32, 32))
        buttonList.add(ImageButton(4, "Mods", "mods.png", dWidth + 64 + 10, dHeight, 32, 32))
        buttonList.add(ImageButton(5, "Quit", "exit.png", dWidth - 96 - 15, dHeight, 32, 32))
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)

        GLUtils.drawImage(ResourceLocation(LiquidCat.CLIENT_NAME.toLowerCase() + "/icons/largelogo.png"), width / 2 - 200, height / 2 - 150, 400, 300)

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> mc.displayGuiScreen(GuiSelectWorld(this))
            1 -> mc.displayGuiScreen(GuiMultiplayer(this))
            2 -> mc.displayGuiScreen(GuiAltManager(this))
            3 -> mc.displayGuiScreen(GuiOptions(this, mc.gameSettings))
            4 -> mc.displayGuiScreen(GuiModsMenu(this))
            5 -> mc.shutdown()
        }
    }
}