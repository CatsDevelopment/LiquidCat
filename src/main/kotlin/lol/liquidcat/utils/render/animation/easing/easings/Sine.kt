/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render.animation.easing.easings

import lol.liquidcat.utils.render.animation.easing.Easing
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object Sine : Easing {
    override fun easeIn(x: Double) = 1 - cos((x * PI) / 2)

    override fun easeOut(x: Double) = sin((x * PI) / 2)

    override fun easeInOut(x: Double) = -(cos(PI * x) - 1) / 2
}