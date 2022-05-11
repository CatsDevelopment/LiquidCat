/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render.animation.easing.easings

import lol.liquidcat.utils.render.animation.easing.Easing
import kotlin.math.pow
import kotlin.math.sin

object Elastic : Easing {

    private const val c2 = (2 * Math.PI) / 3
    private const val c5 = (2 * Math.PI) / 4.5

    override fun easeIn(x: Double): Double {
        return -(2.0.pow(10 * x - 10)) * sin((x * 10 - 10.75) * c2)
    }

    override fun easeOut(x: Double): Double {
        return 2.0.pow(-10 * x) * sin((x * 10 - 0.75) * c2) + 1
    }

    override fun easeInOut(x: Double): Double {
        return if (x < 0.5)
            -(2.0.pow(20 * x - 10) * sin((20 * x - 11.125) * c5)) / 2
        else
            (2.0.pow(-20 * x + 10) * sin((20 * x - 11.125) * c5)) / 2 + 1
    }
}