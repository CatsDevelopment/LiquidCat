/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render.animation.easing.easings

import lol.liquidcat.utils.render.animation.easing.Easing
import kotlin.math.pow

object Expo : Easing {
    override fun easeIn(x: Double): Double {
        return 2.0.pow(10 * x - 10)
    }

    override fun easeOut(x: Double): Double {
        return 1 - 2.0.pow(-10 * x)
    }

    override fun easeInOut(x: Double): Double {
        return if (x < 0.5)
            2.0.pow(20 * x - 10) / 2
        else
            (2 - 2.0.pow(-20 * x + 10)) / 2
    }
}