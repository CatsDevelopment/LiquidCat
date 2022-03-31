/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.login

class MinecraftAccount @JvmOverloads constructor(val name: String, val password: String? = null, var accountName: String? = null) {

    val isCracked: Boolean
        get() = password.isNullOrEmpty()
}