/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
@file:JvmName("MathUtils")

package lol.liquidcat.utils

import java.math.RoundingMode
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

const val DEGREES_TO_RADIANS = 0.017453292519943295

const val RADIANS_TO_DEGREES = 57.29577951308232

/**
 * Rounds double
 *
 * @param x Number of decimals
 */
fun Double.round(x: Int): Double {
    require(x >= 0)
    return this.toBigDecimal().setScale(x, RoundingMode.HALF_UP).toDouble()
}

/**
 * Converts double to radians
 */
fun Double.toRadians() = this * DEGREES_TO_RADIANS

/**
 * Converts double to degrees
 */
fun Double.toDegrees() = this * RADIANS_TO_DEGREES

/**
 * Calculates Gaussian value in one dimension
 *
 * [Assignment information](https://en.wikipedia.org/wiki/Gaussian_blur)
 */
fun gaussian(x: Int, sigma: Float): Float {
    val s = sigma.pow(2) * 2

    return (1f / (sqrt(PI.toFloat() * s))) * exp(-(x * x) / s)
}