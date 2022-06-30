/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils

object StringUtils {
    fun toCompleteString(args: Array<String>, start: Int): String {
        return if (args.size <= start)
            ""
        else
            args.copyOfRange(start, args.size).joinToString(" ")
    }
}