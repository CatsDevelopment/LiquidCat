/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render.animation.easing.easings

import lol.liquidcat.utils.render.animation.easing.Easing
import kotlin.math.pow

object Quint : Easing {
    override fun easeIn(x: Double) = x.pow(5)

    override fun easeOut(x: Double) = 1 - (1 - x).pow(5)

    override fun easeInOut(x: Double): Double {
        return if (x < 0.5) 16 * x.pow(5) else 1 - (-2 * x + 2).pow(5) / 2
    }
}