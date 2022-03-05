/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.item

import net.minecraft.enchantment.Enchantment
import net.minecraft.item.ItemStack

/**
 * Returns the [enchantment] level if it was found on this item
 *
 * If it was not found it returns 0
 */
fun ItemStack?.getEnchantment(enchantment: Enchantment): Int {
    val enchantments = this?.enchantmentTagList ?: return 0

    for (i in 0 until enchantments.tagCount()) {
        val tagCompound = enchantments.getCompoundTagAt(i)

        if (tagCompound.hasKey("id") && tagCompound.getShort("id").toInt() == enchantment.effectId)
            return tagCompound.getShort("lvl").toInt()
    }

    return 0
}

/**
 * Returns the number of enchantments on this item
 */
fun ItemStack?.getEnchantmentCount(): Int {
    val enchantments = this?.enchantmentTagList ?: return 0
    var count = 0

    for (i in 0 until enchantments.tagCount()) {
        val tagCompound = enchantments.getCompoundTagAt(i)

        if (tagCompound.hasKey("id")) count++
    }

    return count
}