/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render

import net.minecraft.util.ChatAllowedCharacters
import java.awt.Color
import java.util.*
import kotlin.math.abs
import kotlin.math.sin

object ColorUtils {

    @JvmField
    val hexColors = IntArray(16)

    init {
        repeat(16) { i ->
            val baseColor = (i shr 3 and 1) * 85

            val red = (i shr 2 and 1) * 170 + baseColor + if (i == 6) 85 else 0
            val green = (i shr 1 and 1) * 170 + baseColor
            val blue = (i and 1) * 170 + baseColor

            hexColors[i] = red and 255 shl 16 or (green and 255 shl 8) or (blue and 255)
        }
    }

    fun Color.applyAlpha(alpha: Int): Color {
        return Color(red, green, blue, alpha)
    }

    /**
     * Makes [color] brighter
     *
     * The greater [factor] value, the brighter the color will be
     *
     * @param factor Color brightness factor
     */
    fun brighter(color: Color, factor: Float): Color {
        require(factor in 0f..1f) { "Color factor should be between 0 and 1" }

        val red = color.red + ((255 - color.red) * factor).toInt()
        val green = color.green + ((255 - color.green) * factor).toInt()
        val blue = color.blue + ((255 - color.blue) * factor).toInt()

        return Color(red, green, blue, color.alpha)
    }

    /**
     * Makes [color] darker
     *
     * The greater [factor] value, the darker the color will be
     *
     * @param factor Color darkness factor
     */
    fun darker(color: Color, factor: Float): Color {
        require(factor in 0f..1f) { "Color factor should be between 0 and 1" }

        val red = (color.red * (1 - factor)).toInt()
        val green = (color.green * (1 - factor)).toInt()
        val blue = (color.blue * (1 - factor)).toInt()

        return Color(red, green, blue, color.alpha)
    }

    /**
     * Mixes two colors
     *
     * @param a First color
     * @param b Second color
     */
    fun mix(a: Color, b: Color, factor: Float): Color {
        require(factor in 0f..1f) { "Color factor should be between 0 and 1" }

        return Color(
            (a.red * factor + b.red * (1 - factor)).toInt(),
            (a.green * factor + b.green * (1 - factor)).toInt(),
            (a.blue * factor + b.blue * (1 - factor)).toInt()
        )
    }

    /**
     * Smoothly fades between two colors
     *
     * @param a First color
     * @param b Second color
     * @param speed Fade speed
     * @param offset Fade offset
     */
    fun fade(a: Color, b: Color, speed: Double = 0.001, offset: Double = 0.0): Color {

        val time = System.currentTimeMillis() * speed + offset
        val factor = abs(sin(time))

        return mix(a, b, factor.toFloat())
    }

    fun rainbow(speed: Double = 0.0005, offset: Double = 0.0, alpha: Int = 255): Color {

        val time = (System.currentTimeMillis() * speed + offset) % 1.0
        val c = Color.getHSBColor(time.toFloat(), 1f, 1f)

        return Color(c.red, c.green, c.blue, alpha)
    }

    /**
     * warning: skidded form fdp!!!
     */
    fun hsbTransition(from: Float, to: Float, angle: Int, s: Float = 1f, b: Float = 1f): Color {
        return Color.getHSBColor(
            if (angle < 180) from + (to - from) * (angle / 180f)
            else from + (to - from) * (-(angle - 360) / 180f), s, b)
    }

    @JvmStatic
    fun translateAlternateColorCodes(textToTranslate: String): String {
        val chars = textToTranslate.toCharArray()

        for (i in 0 until chars.size - 1) {
            if (chars[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".contains(chars[i + 1], true)) {
                chars[i] = '§'
                chars[i + 1] = Character.toLowerCase(chars[i + 1])
            }
        }

        return String(chars)
    }

    fun randomMagicText(text: String): String {
        val stringBuilder = StringBuilder()
        val allowedCharacters = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000"

        for (c in text.toCharArray()) {
            if (ChatAllowedCharacters.isAllowedCharacter(c)) {
                val index = Random().nextInt(allowedCharacters.length)
                stringBuilder.append(allowedCharacters.toCharArray()[index])
            }
        }

        return stringBuilder.toString()
    }
}