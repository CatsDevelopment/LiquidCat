package net.ccbluex.liquidbounce.ui.client

import lol.liquidcat.LiquidCat
import lol.liquidcat.utils.render.GLUtils
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import java.awt.Color

class ImageButton(val id: Int, val name: String, private val icon: String, val x: Int, val y: Int, width: Int, height: Int) : GuiButton(id, x, y, width, height, name) {

    private var cut = 0f

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        if (visible) {
            hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height

            val delta = GLUtils.deltaTime

            GlStateManager.pushMatrix()

            if (enabled && hovered) {
                cut += 0.05f * delta
                if (cut >= 10) cut = 10f
            } else {
                cut -= 0.05f * delta
                if (cut <= 0) cut = 0f
            }

            GlStateManager.translate(0f, -cut, 0f)

            if (hovered)
                Fonts.nunito.drawCenteredString(name, x + width.toFloat() / 2, y - 10f, Color.WHITE.rgb, false)

            GLUtils.drawImage(
                ResourceLocation("${LiquidCat.CLIENT_NAME.toLowerCase()}/icons/$icon"),
                x, y, width, height
            )
            GlStateManager.popMatrix()

            mouseDragged(mc, mouseX, mouseY)
        }
    }
}