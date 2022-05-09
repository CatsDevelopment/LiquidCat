/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.ui.client.hud.element.elements

import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleManager
import lol.liquidcat.ui.client.hud.designer.GuiHudDesigner
import lol.liquidcat.ui.client.hud.element.Border
import lol.liquidcat.ui.client.hud.element.Element
import lol.liquidcat.ui.client.hud.element.ElementInfo
import lol.liquidcat.ui.client.hud.element.Side
import lol.liquidcat.ui.client.hud.element.Side.Horizontal
import lol.liquidcat.ui.client.hud.element.Side.Vertical
import lol.liquidcat.utils.mc
import lol.liquidcat.utils.render.AnimationUtils
import lol.liquidcat.utils.render.color.ColorUtils
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.value.*
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.minecraft.client.renderer.GlStateManager
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

    private val fadeOffset by FloatValue("Fade-Offset", 0.1f, 0.1f..1f)
    private val fadeSpeed by FloatValue("Fade-Speed", 1f, 1f..10f)

    private val rainbowSpeed by FloatValue("Rainbow-Speed", 1f, 1f..10f)

    private val textRed by IntValue("Text-Red", 53, 0..255)
    private val textGreen by IntValue("Text-Green", 111, 0..255)
    private val textBlue by IntValue("Text-Blue", 255, 0..255)

    private val textRed2 by IntValue("Text-Red-2", 120, 0..255)
    private val textGreen2 by IntValue("Text-Green-2", 255, 0..255)
    private val textBlue2 by IntValue("Text-Blue-2", 174, 0..255)

    private val textMode by ListValue("TextMode", arrayOf("Normal", "UpperCase", "LowerCase"), "Normal")

    private val tags by BoolValue("Tags", true)
    private val shadow by BoolValue("ShadowText", true)

    private val bgRed by IntValue("Background-R", 0, 0..255)
    private val bgGreen by IntValue("Background-G", 0, 0..255)
    private val bgBlue by IntValue("Background-B", 0, 0..255)
    private val bgAlpha by IntValue("Background-Alpha", 160, 0..255)

    private val font by FontValue("Font", Fonts.nunito35)

    private var x2 = 0
    private var y2 = 0f

    private var modules = emptyList<Module>()

    override fun drawElement(): Border? {
        AWTFontRenderer.assumeNonVolatile = true

        // Slide animation - update every render
        val delta = GLUtils.deltaTime

        for (module in ModuleManager.modules) {
            if (module.hide || (!module.state && module.slide == 0f)) continue

            var displayString = if (!tags) module.name else module.tagName

            displayString = when (textMode) {
                "UpperCase" -> displayString.toUpperCase()
                "LowerCase" -> displayString.toLowerCase()
                else -> displayString
            }

            val width = font.getStringWidth(displayString)

            if (module.state) {
                if (module.slide < width) {
                    module.slide = AnimationUtils.easeOut(module.slideStep, width.toFloat()) * width
                    module.slideStep += delta / 4f
                }
            } else if (module.slide > 0) {
                module.slide = AnimationUtils.easeOut(module.slideStep, width.toFloat()) * width
                module.slideStep -= delta / 4f
            }

            module.slide = module.slide.coerceIn(0f, width.toFloat())
            module.slideStep = module.slideStep.coerceIn(0f, width.toFloat())
        }

        val textColor = Color(textRed, textGreen, textBlue)
        val textColor2 = Color(textRed2, textGreen2, textBlue2)
        val bgColor = Color(bgRed, bgGreen, bgBlue, bgAlpha).rgb
        val textSpacer = font.FONT_HEIGHT.toFloat()

        when (side.horizontal) {
            Horizontal.RIGHT, Horizontal.MIDDLE -> {
                modules.forEachIndexed { index, module ->
                    var displayString = if (!tags) module.name else module.tagName

                    displayString = when (textMode) {
                        "UpperCase" -> displayString.toUpperCase()
                        "LowerCase" -> displayString.toLowerCase()
                        else -> displayString
                    }

                    val xPos = -module.slide - 2
                    val yPos = (if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer) *
                            if (side.vertical == Vertical.DOWN) index + 1 else index

                    // Draws background rect
                    GLUtils.drawRect(xPos - 2, yPos - 1, 0f, yPos + textSpacer - 1, bgColor)

                    // Draws module name
                    font.drawString(displayString, xPos, yPos, when (textColorMode) {
                        "Rainbow" -> ColorUtils.rainbow(
                            speed = 0.0005 * rainbowSpeed.toDouble(),
                            offset = index * 0.95
                        ).rgb
                        "Fade" -> ColorUtils.fade(
                            textColor,
                            textColor2,
                            speed = 0.001 * fadeSpeed.toDouble(),
                            offset = index * fadeOffset.toDouble()
                        ).rgb
                        else -> textColor.rgb
                    }, shadow)
                }
            }

            Horizontal.LEFT -> {
                modules.forEachIndexed { index, module ->
                    var displayString = if (!tags) module.name else module.tagName

                    displayString = when (textMode) {
                        "UpperCase" -> displayString.toUpperCase()
                        "LowerCase" -> displayString.toLowerCase()
                        else -> displayString
                    }

                    val width = font.getStringWidth(displayString)
                    val xPos = -(width - module.slide) + 2
                    val yPos = (if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer) *
                            if (side.vertical == Vertical.DOWN) index + 1 else index

                    // Draws background rect
                    GLUtils.drawRect(0f, yPos - 1, xPos + width + 2, yPos + textSpacer - 1, bgColor)

                    // Draws module name
                    font.drawString(displayString, xPos, yPos, when (textColorMode) {
                        "Rainbow" -> ColorUtils.rainbow(
                            speed = 0.0005 * rainbowSpeed.toDouble(),
                            offset = index * 0.95
                        ).rgb
                        "Fade" -> ColorUtils.fade(
                            textColor,
                            textColor2,
                            speed = 0.001 * fadeSpeed.toDouble(),
                            offset = index * fadeOffset.toDouble()
                        ).rgb
                        else -> textColor.rgb
                    }, shadow)
                }
            }
        }

        // Draw border
        if (mc.currentScreen is GuiHudDesigner) {
            x2 = Int.MIN_VALUE

            if (modules.isEmpty()) {
                return if (side.horizontal == Horizontal.LEFT)
                    Border(0f, -1f, 20F, 20f)
                else
                    Border(0f, -1f, -20f, 20f)
            }

            for (module in modules) {
                when (side.horizontal) {
                    Horizontal.RIGHT, Horizontal.MIDDLE -> {
                        val xPos = -module.slide.toInt() - 4
                        if (x2 == Int.MIN_VALUE || xPos < x2) x2 = xPos
                    }
                    Horizontal.LEFT -> {
                        val xPos = module.slide.toInt() + 4
                        if (x2 == Int.MIN_VALUE || xPos > x2) x2 = xPos
                    }
                }
            }
            y2 = (if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer) * modules.size

            return Border(0f, -1f, x2.toFloat(), y2 - 1f)
        }

        AWTFontRenderer.assumeNonVolatile = false
        GlStateManager.resetColor()
        return null
    }

    override fun updateElement() {
        modules = ModuleManager.modules
                .filter { !it.hide && it.slide > 0 }
                .sortedBy { -font.getStringWidth(
                    if (tags)
                        when (textMode) {
                            "UpperCase" -> it.tagName.toUpperCase()
                            "LowerCase" -> it.tagName.toLowerCase()
                            else -> it.tagName
                        }
                    else
                        when (textMode) {
                            "UpperCase" -> it.name.toUpperCase()
                            "LowerCase" -> it.name.toLowerCase()
                            else -> it.name
                        }
                )}
    }
}