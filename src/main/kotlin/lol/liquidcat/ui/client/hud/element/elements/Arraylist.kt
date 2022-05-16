/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.ui.client.hud.element.elements

import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleManager
import lol.liquidcat.ui.client.hud.element.Border
import lol.liquidcat.ui.client.hud.element.Element
import lol.liquidcat.ui.client.hud.element.ElementInfo
import lol.liquidcat.ui.client.hud.element.Side
import lol.liquidcat.ui.client.hud.element.Side.Horizontal
import lol.liquidcat.ui.client.hud.element.Side.Vertical
import lol.liquidcat.utils.render.color.ColorUtils
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.value.*
import net.ccbluex.liquidbounce.ui.font.Fonts
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * CustomHUD Arraylist element
 *
 * Shows a list of enabled modules
 */
@ElementInfo(name = "Arraylist", single = true)
class Arraylist(x: Double = 5.0, y: Double = 5.0, scale: Float = 1f,
                side: Side = Side(Horizontal.RIGHT, Vertical.UP)
) : Element(x, y, scale, side) {

    private val textColorMode by ListValue("Text-Color", arrayOf("Static", "Fade", "Rainbow"), "Fade")

    private val fadeOffset by FloatValue("Fade-Offset", 0.2f, 0.1f..1f)
    private val fadeSpeed by FloatValue("Fade-Speed", 2f, 1f..10f)

    private val rainbowSpeed by FloatValue("Rainbow-Speed", 1f, 1f..10f)

    private val textRed by IntValue("Text-Red", 50, 0..255)
    private val textGreen by IntValue("Text-Green", 80, 0..255)
    private val textBlue by IntValue("Text-Blue", 255, 0..255)

    private val textRed2 by IntValue("Text-Red-2", 25, 0..255)
    private val textGreen2 by IntValue("Text-Green-2", 45, 0..255)
    private val textBlue2 by IntValue("Text-Blue-2", 150, 0..255)

    private val textMode by ListValue("TextMode", arrayOf("Normal", "UpperCase", "LowerCase"), "Normal")

    private val tags by BoolValue("Tags", true)
    private val shadow by BoolValue("ShadowText", false)

    private val bgRed by IntValue("Background-R", 0, 0..255)
    private val bgGreen by IntValue("Background-G", 0, 0..255)
    private val bgBlue by IntValue("Background-B", 50, 0..255)
    private val bgAlpha by IntValue("Background-Alpha", 45, 0..255)

    private val blur by BoolValue("Blur", true)

    private val font by FontValue("Font", Fonts.nunito35)

    private var modules = emptyList<Module>()

    override fun drawElement(): Border {

        if (blur) {
            GLUtils.blur(10) {

                GL11.glPushMatrix()

                modules.forEachIndexed { index, module ->

                    val text = getModuleText(module)
                    val width = font.getStringWidth(text)

                    GL11.glPushMatrix()

                    GL11.glTranslatef(
                        -width + ((1f - module.slideAnim.value.toFloat()) * width),
                        (font.FONT_HEIGHT + 4f) * index,
                        0f
                    )

                    GLUtils.drawRect(-2f, -2f, width + 2f, font.FONT_HEIGHT + 2f, -1)

                    GL11.glPopMatrix()
                }

                GL11.glPopMatrix()
            }
        }

        GL11.glPushMatrix()

        modules.forEachIndexed { index, module ->

            module.slideAnim.update()

            val text = getModuleText(module)
            val width = font.getStringWidth(text)

            GL11.glPushMatrix()

            GL11.glTranslatef(
                -width + ((1f - module.slideAnim.value.toFloat()) * width),
                (font.FONT_HEIGHT + 4f) * index,
                0f
            )

            GLUtils.drawRect(-2f, -2f, width + 2f, font.FONT_HEIGHT + 2f, Color(bgRed, bgGreen, bgBlue, bgAlpha).rgb)

            font.drawString(text, 0f, font.FONT_HEIGHT / 2 - 4f, when (textColorMode) {

                "Fade" -> ColorUtils.fade(
                    Color(textRed, textGreen, textBlue),
                    Color(textRed2, textGreen2, textBlue2),
                    0.001 * fadeSpeed.toDouble(),
                    index * fadeOffset.toDouble()
                ).rgb

                "Rainbow" -> ColorUtils.rainbow(
                    speed = 0.0005 * rainbowSpeed.toDouble(),
                    offset = index * 0.95
                ).rgb

                else -> Color(textRed, textGreen, textBlue).rgb
            }, shadow)

            GL11.glPopMatrix()
        }

        GL11.glPopMatrix()

        // Draw border
        return Border(-font.getStringWidth(getModuleText(modules.first())).toFloat() - 2f, -2f, 2f, modules.size * (font.FONT_HEIGHT + 4f) - 2f)
    }

    private fun getModuleText(module: Module): String {
        val name = if (tags) module.tagName else module.name
        return when (textMode) {
            "UpperCase" -> name.toUpperCase()
            "LowerCase" -> name.toLowerCase()

            else -> name
        }
    }

    override fun updateElement() {
        modules = ModuleManager.modules
            .filter { !it.hide && it.state }
            .sortedBy { -font.getStringWidth(getModuleText(it)) }
    }
}