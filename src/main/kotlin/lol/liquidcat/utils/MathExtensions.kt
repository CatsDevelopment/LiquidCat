/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
@file:JvmName("MathUtils")

package lol.liquidcat.utils

import java.math.RoundingMode

const val DEGREES_TO_RADIANS = 0.017453292519943295

const val RADIANS_TO_DEGREES = 57.29577951308232

fun Double.round(places: Int): Double {
    require(places >= 0)
    return this.toBigDecimal().setScale(places, RoundingMode.HALF_UP).toDouble()
}

fun Double.toRadians() = this * DEGREES_TO_RADIANS

fun Double.toDegrees() = this * RADIANS_TO_DEGREES