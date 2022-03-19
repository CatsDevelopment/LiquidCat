/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.timer

class MSTimer {

    private var time = -1L

    fun hasTimePassed(ms: Long): Boolean {
        return System.currentTimeMillis() >= time + ms
    }

    fun hasTimeLeft(ms: Long): Long {
        return ms + time - System.currentTimeMillis()
    }

    fun reset() {
        time = System.currentTimeMillis()
    }
}