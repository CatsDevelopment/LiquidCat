/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render.animation

import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.utils.render.animation.easing.Direction
import lol.liquidcat.utils.render.animation.easing.Easing

/**
 * Animation class
 *
 * Used to create smooth transitions
 *
 * @param speed Animation speed
 * @param style Animation style
 * @param direction Animation direction
 */
class Animation(val speed: Double, val style: Easing, private val direction: Direction) {

    /**
     * Animation value
     */
    var value = 0.0

    /**
     * Animation time
     *
     * Must be between 0 and 1
     */
    private var time = 0.0
        set(v) {
            field = v.coerceIn(0.0..1.0)

            // Converts animation time to animation value
            value = when (direction) {
                Direction.IN -> style.easeIn(time)
                Direction.OUT -> style.easeOut(time)
                Direction.INOUT -> style.easeInOut(time)
            }
        }

    /**
     * Updates animation
     */
    fun update(reversed: Boolean = false) {

        // Sets animation time
        if (reversed)
            time -= 0.00001 * speed * GLUtils.deltaTime
        else
            time += 0.00001 * speed * GLUtils.deltaTime
    }

    /**
     * Resets animation
     */
    fun reset() {
        time = 0.0
    }
}