/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.ui.client

import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.GuiTextField

class GuiPasswordField(componentId: Int, fontRenderer: FontRenderer, x: Int, y: Int, par5Width: Int, par6Height: Int) : GuiTextField(componentId, fontRenderer, x, y, par5Width, par6Height) {

    /**
     * Draw text box
     */
    override fun drawTextBox() {
        val realText = text

        text = buildString {
            for (i in text.indices) append('*')
        }

        super.drawTextBox()

        text = realText
    }
}