/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.timer

class TickTimer {

    private var tick = 0

    fun hasTimePassed(ticks: Int): Boolean {
        return tick >= ticks
    }

    fun update() = tick++

    fun reset() {
        tick = 0
    }
}