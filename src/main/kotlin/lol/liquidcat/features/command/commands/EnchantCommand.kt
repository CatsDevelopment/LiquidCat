/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.command.commands

import lol.liquidcat.features.command.Command
import net.minecraft.enchantment.Enchantment

object EnchantCommand : Command("enchant", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 2) {
            if (mc.playerController.isNotCreative) {
                chat("§c§lError: §3You need to be in creative mode.")
                return
            }

            val item = mc.thePlayer.heldItem

            if (item == null || item.item == null) {
                chat("§c§lError: §3You need to hold an item.")
                return
            }

            val enchantID = try {
                args[1].toInt()
            } catch (e: NumberFormatException) {
                val enchantment = Enchantment.getEnchantmentByLocation(args[1])

                if (enchantment == null) {
                    chat("There is no enchantment with the name '${args[1]}'")
                    return
                }

                enchantment.effectId
            }

            val enchantment = Enchantment.getEnchantmentById(enchantID)
            if (enchantment == null) {
                chat("There is no enchantment with the ID '$enchantID'")
                return
            }

            val level = try {
                args[2].toInt()
            } catch (e: NumberFormatException) {
                chatSyntaxError()
                return
            }

            item.addEnchantment(enchantment, level)
            chat("${enchantment.getTranslatedName(level)} added to ${item.displayName}.")
            return
        }
        chatSyntax("enchant <type> [level]")
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        return when (args.size) {
            1 -> {
                return Enchantment.func_181077_c()
                    .map { it.resourcePath.lowercase() }
                    .filter { it.startsWith(args[0], true) }
            }
            else -> emptyList()
        }
    }
}