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
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.value.*
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color

/**
 * CustomHUD Arraylist element
 *
 * Shows a list of enabled modules
 */
@ElementInfo(name = "Arraylist", single = true)
class Arraylist(x: Double = 1.0, y: Double = 2.0, scale: Float = 1F,
                side: Side = Side(Horizontal.RIGHT, Vertical.UP)
) : Element(x, y, scale, side) {

    private val textColorMode by ListValue("Text-Color", arrayOf("Custom", "Random", "Rainbow"), "Custom")
    
    private val textRed by IntValue("Text-R", 0, 0..255)
    private val textGreen by IntValue("Text-G", 111, 0..255)
    private val textBlue by IntValue("Text-B", 255, 0..255)
    
    private val rectColorMode by ListValue("Rect-Color", arrayOf("Custom", "Random", "Rainbow"), "Rainbow")
    
    private val rectRed by IntValue("Rect-R", 255, 0..255)
    private val rectGreen by IntValue("Rect-G", 255, 0..255)
    private val rectBlue by IntValue("Rect-B", 255, 0..255)
    private val rectAlpha by IntValue("Rect-Alpha", 255, 0..255)
    
    private val saturation by FloatValue("Random-Saturation", 0.9f, 0f..1f)
    private val brightness by FloatValue("Random-Brightness", 1f, 0f..1f)
    
    private val tags by BoolValue("Tags", true)
    private val shadow by BoolValue("ShadowText", true)
    
    private val bgColorMode by ListValue("Background-Color", arrayOf("Custom", "Random", "Rainbow"), "Custom")
    
    private val bgRed by IntValue("Background-R", 0, 0..255)
    private val bgGreen by IntValue("Background-G", 0, 0..255)
    private val bgBlue by IntValue("Background-B", 0, 0..255)
    private val bgAlpha by IntValue("Background-Alpha", 0, 0..255)
    
    private val rectMode by ListValue("Rect", arrayOf("None", "Left", "Right"), "None")
    private val upperCase by BoolValue("UpperCase", false)
    private val space by FloatValue("Space", 0f, 0f..5f)
    private val textHeight by FloatValue("TextHeight", 11f, 1f..20f)
    private val textY by FloatValue("TextY", 1f, 0f..20f)
    private val tagsArrayColor by BoolValue("TagsArrayColor", false)
    private val font by FontValue("Font", Fonts.nunitoBold40)

    private var x2 = 0
    private var y2 = 0F

    private var modules = emptyList<Module>()

    override fun drawElement(): Border? {
        AWTFontRenderer.assumeNonVolatile = true

        // Slide animation - update every render
        val delta = GLUtils.deltaTime

        for (module in ModuleManager.modules) {
            if (module.hide || (!module.state && module.slide == 0F)) continue

            var displayString = if (!tags)
                module.name
            else if (tagsArrayColor)
                module.colorlessTagName
            else module.tagName

            if (upperCase)
                displayString = displayString.toUpperCase()

            val width = font.getStringWidth(displayString)

            if (module.state) {
                if (module.slide < width) {
                    module.slide = AnimationUtils.easeOut(module.slideStep, width.toFloat()) * width
                    module.slideStep += delta / 4F
                }
            } else if (module.slide > 0) {
                module.slide = AnimationUtils.easeOut(module.slideStep, width.toFloat()) * width
                module.slideStep -= delta / 4F
            }

            module.slide = module.slide.coerceIn(0F, width.toFloat())
            module.slideStep = module.slideStep.coerceIn(0F, width.toFloat())
        }

        val textColor = Color(textRed, textGreen, textBlue, 1).rgb
        val rectColor = Color(rectRed, rectGreen, rectBlue, rectAlpha).rgb
        val bgColor = Color(bgRed, bgGreen, bgBlue, bgAlpha).rgb
        val textSpacer = textHeight + space

        when (side.horizontal) {
            Horizontal.RIGHT, Horizontal.MIDDLE -> {
                modules.forEachIndexed { index, module ->
                    var displayString = if (!tags)
                        module.name
                    else if (tagsArrayColor)
                        module.colorlessTagName
                    else module.tagName

                    if (upperCase)
                        displayString = displayString.toUpperCase()

                    val xPos = -module.slide - 2
                    val yPos = (if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer) *
                            if (side.vertical == Vertical.DOWN) index + 1 else index
                    val moduleColor = Color.getHSBColor(module.hue, saturation, brightness).rgb

                    GLUtils.drawRect(
                            xPos - if (rectMode.equals("right", true)) 5 else 2,
                            yPos,
                            if (rectMode.equals("right", true)) -3F else 0F,
                            yPos + textHeight, when {
                        bgColorMode.equals("Rainbow", ignoreCase = true) -> ColorUtils.rainbow(400000000L * index).rgb
                        bgColorMode.equals("Random", ignoreCase = true) -> moduleColor
                        else -> bgColor
                    }
                    )

                    font.drawString(displayString, xPos - if (rectMode.equals("right", true)) 3 else 0, yPos + textY, when {
                        textColorMode.equals("Rainbow", ignoreCase = true) -> ColorUtils.rainbow(400000000L * index).rgb
                        textColorMode.equals("Random", ignoreCase = true) -> moduleColor
                        else -> textColor
                    }, shadow)

                    if (!rectMode.equals("none", true)) {
                        val rectColor = when {
                            rectColorMode.equals("Rainbow", ignoreCase = true) -> ColorUtils.rainbow(400000000L * index).rgb
                            rectColorMode.equals("Random", ignoreCase = true) -> moduleColor
                            else -> rectColor
                        }

                        when {
                            rectMode.equals("left", true) -> GLUtils.drawRect(xPos - 5, yPos, xPos - 2, yPos + textHeight,
                                    rectColor)
                            rectMode.equals("right", true) -> GLUtils.drawRect(-3F, yPos, 0F,
                                    yPos + textHeight, rectColor)
                        }
                    }
                }
            }

            Horizontal.LEFT -> {
                modules.forEachIndexed { index, module ->
                    var displayString = if (!tags)
                        module.name
                    else if (tagsArrayColor)
                        module.colorlessTagName
                    else module.tagName

                    if (upperCase)
                        displayString = displayString.toUpperCase()

                    val width = font.getStringWidth(displayString)
                    val xPos = -(width - module.slide) + if (rectMode.equals("left", true)) 5 else 2
                    val yPos = (if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer) *
                            if (side.vertical == Vertical.DOWN) index + 1 else index
                    val moduleColor = Color.getHSBColor(module.hue, saturation, brightness).rgb

                    GLUtils.drawRect(
                            0F,
                            yPos,
                            xPos + width + if (rectMode.equals("right", true)) 5 else 2,
                            yPos + textHeight, when {
                        bgColorMode.equals("Rainbow", ignoreCase = true) -> ColorUtils.rainbow(400000000L * index).rgb
                        bgColorMode.equals("Random", ignoreCase = true) -> moduleColor
                        else -> bgColor
                    }
                    )

                    font.drawString(displayString, xPos, yPos + textY, when {
                        textColorMode.equals("Rainbow", ignoreCase = true) -> ColorUtils.rainbow(400000000L * index).rgb
                        textColorMode.equals("Random", ignoreCase = true) -> moduleColor
                        else -> textColor
                    }, shadow)

                    if (!rectMode.equals("none", true)) {
                        val rectColor = when {
                            rectColorMode.equals("Rainbow", ignoreCase = true) -> ColorUtils.rainbow(400000000L * index).rgb
                            rectColorMode.equals("Random", ignoreCase = true) -> moduleColor
                            else -> rectColor
                        }

                        when {
                            rectMode.equals("left", true) -> GLUtils.drawRect(0F,
                                    yPos - 1, 3F, yPos + textHeight, rectColor)
                            rectMode.equals("right", true) ->
                                GLUtils.drawRect(xPos + width + 2, yPos, xPos + width + 2 + 3,
                                        yPos + textHeight, rectColor)
                        }
                    }
                }
            }
        }

        // Draw border
        if (mc.currentScreen is GuiHudDesigner) {
            x2 = Int.MIN_VALUE

            if (modules.isEmpty()) {
                return if (side.horizontal == Horizontal.LEFT)
                    Border(0F, -1F, 20F, 20F)
                else
                    Border(0F, -1F, -20F, 20F)
            }

            for (module in modules) {
                when (side.horizontal) {
                    Horizontal.RIGHT, Horizontal.MIDDLE -> {
                        val xPos = -module.slide.toInt() - 2
                        if (x2 == Int.MIN_VALUE || xPos < x2) x2 = xPos
                    }
                    Horizontal.LEFT -> {
                        val xPos = module.slide.toInt() + 14
                        if (x2 == Int.MIN_VALUE || xPos > x2) x2 = xPos
                    }
                }
            }
            y2 = (if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer) * modules.size

            return Border(0F, 0F, x2 - 7F, y2 - if(side.vertical == Vertical.DOWN) 1F else 0F)
        }

        AWTFontRenderer.assumeNonVolatile = false
        GlStateManager.resetColor()
        return null
    }

    override fun updateElement() {
        modules = ModuleManager.modules
                .filter { !it.hide && it.slide > 0 }
                .sortedBy { -font.getStringWidth(if (upperCase) (if (!tags) it.name else if (tagsArrayColor) it.colorlessTagName else it.tagName).toUpperCase() else if (!tags) it.name else if (tagsArrayColor) it.colorlessTagName else it.tagName) }
    }
}