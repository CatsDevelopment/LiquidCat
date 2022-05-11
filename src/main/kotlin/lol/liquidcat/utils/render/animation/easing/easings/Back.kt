/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render.animation.easing.easings

import lol.liquidcat.utils.render.animation.easing.Easing
import kotlin.math.pow

object Back : Easing {

    private const val c1 = 1.70158
    private const val c3 = c1 + 1
    private const val c4 = c1 * 1.525

    override fun easeIn(x: Double): Double {
        return c3 * x.pow(3) - c1 * x.pow(2)
    }

    override fun easeOut(x: Double): Double {
        return 1 + c3 * (x - 1).pow(3) + c1 * (x - 1).pow(2)
    }

    override fun easeInOut(x: Double): Double {
        return if (x < 0.5)
            ((2 * x).pow(2) * ((c4 + 1) * 2 * x - c4)) / 2
        else
            ((2 * x - 2).pow(2) * ((c4 + 1) * (x * 2 - 2) + c4) + 2) / 2
    }
}