/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.timer

/**
 * Millisecond timer
 */
class MSTimer {

    private var time = 0L

    /**
     * Checks if timer [time] has passed [x][ms] milliseconds
     */
    fun hasTimePassed(ms: Long) = System.currentTimeMillis() >= time + ms

    /**
     * Resets timer time
     */
    fun reset() {
        time = System.currentTimeMillis()
    }
}