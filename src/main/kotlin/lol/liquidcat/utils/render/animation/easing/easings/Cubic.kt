/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render.animation.easing.easings

import lol.liquidcat.utils.render.animation.easing.Easing
import kotlin.math.pow

object Cubic : Easing {
    override fun easeIn(x: Double) = x.pow(3)

    override fun easeOut(x: Double) = 1 - (1 - x).pow(3)

    override fun easeInOut(x: Double): Double {
        return if (x < 0.5) 4 * x.pow(3) else 1 - (-2 * x + 2).pow(3) / 2
    }
}