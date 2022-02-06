/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.command.shortcuts

import lol.liquidcat.features.command.Command

class Shortcut(val name: String, val script: List<Pair<lol.liquidcat.features.command.Command, Array<String>>>): lol.liquidcat.features.command.Command(name, arrayOf()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        script.forEach { it.first.execute(it.second) }
    }
}
