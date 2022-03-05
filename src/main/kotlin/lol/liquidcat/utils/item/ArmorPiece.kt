/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.item

import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack

class ArmorPiece(val itemStack: ItemStack?, val slot: Int) {
    val armorType: Int
        get() = (itemStack?.item as ItemArmor).armorType
}