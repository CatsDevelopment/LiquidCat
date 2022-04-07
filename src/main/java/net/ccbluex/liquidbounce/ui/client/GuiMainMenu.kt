/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package net.ccbluex.liquidbounce.ui.client

import lol.liquidcat.LiquidCat
import lol.liquidcat.utils.render.GLUtils
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager
import net.minecraft.client.gui.*
import net.minecraft.util.ResourceLocation

class GuiMainMenu : GuiScreen(), GuiYesNoCallback {

    override fun initGui() {
        buttonList.add(ImageButton(0, "Singleplayer", "singleplayer.png", width / 2, height - 40, 32, 32))
        buttonList.add(ImageButton(1, "Multiplayer", "multiplayer.png", width / 2 - 32 - 5, height - 40, 32, 32))
        buttonList.add(ImageButton(2, "Alt Manager", "altmanager.png", width / 2 + 32 + 5, height - 40, 32, 32))
        buttonList.add(ImageButton(3, "Settings", "settings.png", width / 2 - 64 - 10, height - 40, 32, 32))
        buttonList.add(ImageButton(4, "Mods", "mods.png", width / 2 + 64 + 10, height - 40, 32, 32))
        buttonList.add(ImageButton(5, "Quit", "exit.png", width / 2 - 96 - 15, height - 40, 32, 32))

        super.initGui()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        ScaledResolution(mc).scaleFactor

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

    override fun keyTyped(typedChar: Char, keyCode: Int) {}
}