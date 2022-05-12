/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render.animation

import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.utils.render.animation.easing.Direction
import lol.liquidcat.utils.render.animation.easing.Easing

class Animation(val speed: Double, val style: Easing, private val direction: Direction) {

    private var time = 0.0
        set(value) {
            field = value.coerceIn(0.0..1.0)
        }

    var value = 0.0

    fun update() {
        time += 0.00001 * speed * GLUtils.deltaTime

        value = when (direction) {
            Direction.IN -> style.easeIn(time)
            Direction.OUT -> style.easeOut(time)
            Direction.INOUT -> style.easeInOut(time)
        }
    }

    fun reset() {
        time = 0.0
    }
}