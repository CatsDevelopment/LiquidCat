/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render

object AnimationUtils {
    /**
     * In-out-easing function
     *
     * https://github.com/jesusgollonet/processing-penner-easing
     *
     * @param t Current iteration
     * @param d Total iterations
     * @return Eased value
     */
    fun easeOut(t: Float, d: Float): Float {
        var t = t
        return (t / d - 1.also { t = it.toFloat() }) * t * t + 1
    }
}