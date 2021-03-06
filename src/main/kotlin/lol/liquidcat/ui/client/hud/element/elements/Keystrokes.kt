/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.ui.client.hud.element.elements

import lol.liquidcat.ui.client.hud.element.Border
import lol.liquidcat.ui.client.hud.element.Element
import lol.liquidcat.ui.client.hud.element.ElementInfo
import lol.liquidcat.ui.client.hud.element.Align
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.utils.render.color.brighter
import lol.liquidcat.value.IntValue
import net.ccbluex.liquidbounce.ui.font.Fonts
import org.lwjgl.input.Keyboard
import java.awt.Color

@ElementInfo("Keystrokes", true)
class Keystrokes : Element(8.0, 96.0, align = Align(Align.Horizontal.LEFT, Align.Vertical.UP)) {

    private val red by IntValue("Red", 40, 0..255)
    private val green by IntValue("Green", 100, 0..255)
    private val blue by IntValue("Blue", 255, 0..255)
    private val alpha by IntValue("Alpha", 110, 0..255)

    override fun drawElement(): Border {

        val c = Color(red, green, blue, alpha)
        val c2 = c.brighter(0.5f)

        GLUtils.blur(10) {
            GLUtils.drawRoundedRect(0f, 70f, 30f, 100f, 0.3f, Color.WHITE)

            GLUtils.drawRoundedRect(30f + 1f, 70f, 60f + 1f, 100f, 0.3f, Color.WHITE)

            GLUtils.drawRoundedRect(60f + 2f, 70f, 90f + 2f, 100f, 0.3f, Color.WHITE)

            GLUtils.drawRoundedRect(30f + 1f, 40f - 1f, 60f + 1f, 70f - 1f, 0.3f, Color.WHITE)
        }

        GLUtils.drawRoundedRect(0f, 70f, 30f, 100f, 0.3f, if (Keyboard.isKeyDown(Keyboard.KEY_A)) c2 else c)
        Fonts.nunitoExBold40.drawCenteredString("A", 15f, 70f + Fonts.nunitoExBold40.FONT_HEIGHT, Color.WHITE.rgb, false)

        GLUtils.drawRoundedRect(30f + 1f, 70f, 60f + 1f, 100f, 0.3f, if (Keyboard.isKeyDown(Keyboard.KEY_S)) c2 else c)
        Fonts.nunitoExBold40.drawCenteredString("S", 46f, 70f + Fonts.nunitoExBold40.FONT_HEIGHT, Color.WHITE.rgb, false)

        GLUtils.drawRoundedRect(60f + 2f, 70f, 90f + 2f, 100f, 0.3f, if (Keyboard.isKeyDown(Keyboard.KEY_D)) c2 else c)
        Fonts.nunitoExBold40.drawCenteredString("D", 77f, 70f + Fonts.nunitoExBold40.FONT_HEIGHT, Color.WHITE.rgb, false)

        GLUtils.drawRoundedRect(30f + 1f, 40f - 1f, 60f + 1f, 70f - 1f, 0.3f, if (Keyboard.isKeyDown(Keyboard.KEY_W)) c2 else c)
        Fonts.nunitoExBold40.drawCenteredString("W", 46f, 39f + Fonts.nunitoExBold40.FONT_HEIGHT, Color.WHITE.rgb, false)

        return Border(0f, 39f, 92f, 100f)
    }
}