/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render.animation.easing.easings

import lol.liquidcat.utils.render.animation.easing.Easing
import kotlin.math.pow
import kotlin.math.sqrt

object Circ : Easing {
    override fun easeIn(x: Double) = 1 - sqrt(1 - x.pow(2))

    override fun easeOut(x: Double) = sqrt(1 - (x - 1).pow(2))

    override fun easeInOut(x: Double): Double {
        return if (x < 0.5)
            (1 - sqrt(1 - (2 * x).pow(2))) / 2
        else
            (sqrt(1 - (-2 * x + 2).pow(2)) + 1) / 2
    }
}