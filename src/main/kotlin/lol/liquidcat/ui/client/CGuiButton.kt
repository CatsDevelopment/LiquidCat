/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.ui.client

import lol.liquidcat.utils.render.GLUtils.drawRoundedRect
import lol.liquidcat.utils.render.animation.Animation
import lol.liquidcat.utils.render.animation.easing.Direction
import lol.liquidcat.utils.render.animation.easing.easings.Sine
import lol.liquidcat.utils.render.color.brighter
import lol.liquidcat.utils.render.color.darker
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import java.awt.Color

/**
 * Custom Minecraft GUI Button
 *
 * @param id Button ID
 * @param name Button name
 */
class CGuiButton(val id: Int, val name: String, val x: Int, val y: Int, width: Int, height: Int) : GuiButton(id, x, y, width, height, name) {

    private val color = Color(14, 0, 38, 155)
    private val anim = Animation(400.0, Sine, Direction.INOUT)

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        if (visible) {
            hovered = mouseX in xPosition..xPosition + width && mouseY in yPosition..yPosition + height

            val xOffset = anim.value.toFloat() * 15f

            anim.update(!hovered)

            drawRoundedRect(
                xPosition.toFloat(), yPosition.toFloat(),
                xPosition.toFloat() + width - xOffset, yPosition.toFloat() + height,
                0.4f,
                if (enabled)
                    color.darker(anim.value.toFloat())
                else
                    color.brighter(0.2f)
            )

            Fonts.nunitoBold40.drawCenteredString(name, x + width / 2f - xOffset / 2f, y + height / 2f - Fonts.nunitoBold40.FONT_HEIGHT / 2, Color.WHITE.rgb, false)

            mouseDragged(mc, mouseX, mouseY)
        }
    }
}