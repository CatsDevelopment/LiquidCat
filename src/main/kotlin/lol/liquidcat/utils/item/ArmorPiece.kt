package lol.liquidcat.utils.item

import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack


class ArmorPiece(val itemStack: ItemStack?, val slot: Int) {
    val armorType: Int
        get() = (itemStack?.item as ItemArmor).armorType
}