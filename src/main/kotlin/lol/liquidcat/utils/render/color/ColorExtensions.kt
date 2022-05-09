/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render.color

import java.awt.Color

fun Color.applyAlpha(alpha: Int): Color {
    return Color(red, green, blue, alpha)
}

/**
 * Makes color brighter
 *
 * The greater [factor] value, the brighter the color will be
 *
 * @param factor Color brightness factor
 */
fun Color.brighter(factor: Float): Color {
    require(factor in 0f..1f) { "Color factor should be between 0 and 1" }

    val red = red + ((255 - red) * factor).toInt()
    val green = green + ((255 - green) * factor).toInt()
    val blue = blue + ((255 - blue) * factor).toInt()

    return Color(red, green, blue, alpha)
}

/**
 * Makes color darker
 *
 * The greater [factor] value, the darker the color will be
 *
 * @param factor Color darkness factor
 */
fun Color.darker(factor: Float): Color {
    require(factor in 0f..1f) { "Color factor should be between 0 and 1" }

    val red = (red * (1 - factor)).toInt()
    val green = (green * (1 - factor)).toInt()
    val blue = (blue * (1 - factor)).toInt()

    return Color(red, green, blue, alpha)
}