/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package lol.liquidcat.utils.item

import net.minecraft.enchantment.Enchantment
import net.minecraft.item.ItemStack

/**
 * @author MCModding4K
 */
object ItemUtils {

    @JvmStatic
    fun getEnchantment(itemStack: ItemStack?, enchantment: Enchantment): Int {
        if (itemStack == null || itemStack.enchantmentTagList == null || itemStack.enchantmentTagList.hasNoTags()) return 0
        for (i in 0 until itemStack.enchantmentTagList.tagCount()) {
            val tagCompound = itemStack.enchantmentTagList.getCompoundTagAt(i)
            if (tagCompound.hasKey("ench") && tagCompound.getShort("ench")
                    .toInt() == enchantment.effectId || tagCompound.hasKey("id") && tagCompound.getShort("id")
                    .toInt() == enchantment.effectId
            ) return tagCompound.getShort("lvl")
                .toInt()
        }
        return 0
    }

    @JvmStatic
    fun getEnchantmentCount(itemStack: ItemStack?): Int {
        if (itemStack == null || itemStack.enchantmentTagList == null || itemStack.enchantmentTagList.hasNoTags()) return 0
        var c = 0
        for (i in 0 until itemStack.enchantmentTagList.tagCount()) {
            val tagCompound = itemStack.enchantmentTagList.getCompoundTagAt(i)
            if (tagCompound.hasKey("ench") || tagCompound.hasKey("id")) c++
        }
        return c
    }
}