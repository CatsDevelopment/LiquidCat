/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render.animation.easing

interface Easing {
    fun easeIn(x: Double): Double

    fun easeOut(x: Double): Double

    fun easeInOut(x: Double): Double
}

/**
 * Animation directions
 */
enum class Direction {
    IN, OUT, INOUT
}