/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.ui.client.guis

import lol.liquidcat.LiquidCat
import lol.liquidcat.ui.client.CGuiButton
import lol.liquidcat.utils.render.GLUtils
import net.ccbluex.liquidbounce.ui.client.GuiModsMenu
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.minecraft.client.gui.*
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import java.awt.Color

class GuiMainMenu : GuiScreen(), GuiYesNoCallback {

    override fun initGui() {
        val dWidth = width / 2 - 100
        val d2Width = dWidth + 103

        val dHeight = height - 29

        buttonList.add(CGuiButton(0, "Singleplayer", dWidth, dHeight - 52 - 6, 100, 26))
        buttonList.add(CGuiButton(1, "Multiplayer", d2Width, dHeight - 52 - 6, 100, 26))
        buttonList.add(CGuiButton(2, "Alt Manager", dWidth, dHeight - 26 - 3, 100, 26))
        buttonList.add(CGuiButton(3, "Settings", d2Width, dHeight - 26 - 3, 100, 26))
        buttonList.add(CGuiButton(4, "Mods", dWidth, dHeight, 100, 26))
        buttonList.add(CGuiButton(5, "Quit", d2Width, dHeight, 100, 26))
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)

        GlStateManager.enableTexture2D()
        GLUtils.drawImage(ResourceLocation(LiquidCat.CLIENT_NAME.lowercase() + "/images/menu_client_logo.png"), width / 2 - 200, height / 2 - 150, 400, 300)
        GlStateManager.disableTexture2D()

        Fonts.nunito35.drawString("${LiquidCat.CLIENT_NAME} ${LiquidCat.CLIENT_VERSION}", 10f, height - Fonts.nunito40.FONT_HEIGHT * 2 - 5f, Color.WHITE.rgb)
        Fonts.nunito35.drawString("Made by ${LiquidCat.CLIENT_CREATOR}", 10f, height - Fonts.nunito40.FONT_HEIGHT - 5f, Color.WHITE.rgb)

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