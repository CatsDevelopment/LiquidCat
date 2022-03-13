/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.item

import net.minecraft.block.Block
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.ItemStack

/**
 * Returns the enchantment level
 *
 * @param enchantment Enchantment
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
 * Returns the number of enchantments
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

/**
 * Returns the amount of damage to entity
 */
fun ItemStack?.getDamage(): Double {
    val baseDamage = this?.attributeModifiers?.get("generic.attackDamage")?.first()?.amount ?: 0.0

    return baseDamage + (1.25 * this.getEnchantment(Enchantment.sharpness))
}

/**
 * Returns the block digging speed
 *
 * @param block Block
 */
fun ItemStack?.getMineSpeed(block: Block?): Float {
    var speed = this?.getStrVsBlock(block) ?: 1f
    val eff = this.getEnchantment(Enchantment.efficiency)

    if (speed > 1 && eff != 0)
        speed += eff * eff + 1

    return speed
}