/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render.animation.easing.easings

import lol.liquidcat.utils.render.animation.easing.Easing
import kotlin.math.pow

object Bounce : Easing {
    
    private const val n1 = 7.5625
    private const val d1 = 2.75
    
    override fun easeIn(x: Double): Double {
        return 1 - easeOut(1 - x)
    }

    override fun easeOut(x: Double): Double {
        return if (x < 1.0 / d1)
            n1 * x.pow(2.0)
        else if (x < 2.0 / d1)
            n1 * (x - 1.5 / d1).pow(2.0) + 0.75
        else if (x < 2.5 / d1)
            n1 * (x - 2.25 / d1).pow(2.0) + 0.9375
        else
            n1 * (x - 2.625 / d1).pow(2.0) + 0.984375
    }

    override fun easeInOut(x: Double): Double {
        return if (x < 0.5) (1 - easeOut(1 - 2 * x)) / 2 else (1 + easeOut(2 * x - 1)) / 2
    }
}