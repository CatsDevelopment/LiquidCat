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
import lol.liquidcat.utils.mc
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FontValue
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer.Companion.assumeNonVolatile
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.minecraft.client.resources.I18n
import net.minecraft.potion.Potion
import net.minecraft.util.StringUtils

/**
 * CustomHUD effects element
 *
 * Shows a list of active potion effects
 */
@ElementInfo("Effects")
class Effects : Element(2.0, 10.0, align = Align(Align.Horizontal.RIGHT, Align.Vertical.DOWN)) {

    private val amplifier by BoolValue("Amplifier", false)

    private val font by FontValue("Font", Fonts.nunito35)
    private val shadow by BoolValue("Shadow", true)

    override fun drawElement(): Border {

        var y = 0f
        var width = 0f

        assumeNonVolatile = true

        for (effect in mc.thePlayer.activePotionEffects) {

            val potion = Potion.potionTypes[effect.potionID]
            val number = when {
                effect.amplifier == 0 -> "I"
                effect.amplifier == 1 -> "II"
                effect.amplifier == 2 -> "III"
                effect.amplifier == 3 -> "IV"
                effect.amplifier == 4 -> "V"
                effect.amplifier == 5 -> "VI"
                effect.amplifier == 6 -> "VII"
                effect.amplifier == 7 -> "VIII"
                effect.amplifier == 8 -> "IX"
                effect.amplifier == 9 -> "X"
                effect.amplifier > 10 -> "X+"

                // Potion effect amplifier becomes negative when its value is greater than 127
                else -> ""
            }

            val name = "${I18n.format(potion.name)} ${if (amplifier) number else ""}§f - §7${StringUtils.ticksToElapsedTime(effect.duration)}"
            val stringWidth = font.getStringWidth(name).toFloat()

            if (width < stringWidth)
                width = stringWidth

            font.drawString(name, -stringWidth, y, potion.liquidColor, shadow)
            y -= font.FONT_HEIGHT
        }

        assumeNonVolatile = false

        if (width == 0f) width = 40f

        if (y == 0f) y = -10f

        return Border(2f, font.FONT_HEIGHT.toFloat(), -width - 2f, y + font.FONT_HEIGHT - 2f)
    }
}