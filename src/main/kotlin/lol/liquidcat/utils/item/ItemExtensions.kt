/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.item

import lol.liquidcat.utils.mc
import net.minecraft.block.Block
import net.minecraft.enchantment.Enchantment
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack

/**
 * Returns [enchantment] level
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
 * Returns [block] digging speed
 */
fun ItemStack?.getMineSpeed(block: Block?): Float {
    var speed = this?.getStrVsBlock(block) ?: 1f
    val eff = this.getEnchantment(Enchantment.efficiency)

    if (speed > 1 && eff != 0)
        speed += eff * eff + 1

    return speed
}

// val ARMOR_SLOTS = 5..8
private val HOTBAR_SLOTS = 36..44
private val INVENTORY_SLOTS = 9..35
// val FULL_INVENTORY_SLOTS = 9..44
// val CRAFTING_SLOTS = 1..4
// val CRAFTED_ITEM_SLOT = 0

private inline fun findSlot(range: IntRange, filter: (ItemStack?) -> Boolean): Int {
    return range.firstOrNull { filter(mc.thePlayer.inventoryContainer.getSlot(it).stack) } ?: -1
}

/**
 * Searches for a slot in the inventory with [item] and returns its index
 *
 * @param item Searched item
 */
fun findInventorySlot(item: Item) = findInventorySlot { it?.item == item }

private fun findInventorySlot(filter: (ItemStack?) -> Boolean) = findSlot(INVENTORY_SLOTS, filter)

/**
 * Searches for a slot in the hotbar with [item] and returns its index
 *
 * @param item Searched item
 */
fun findHotbarSlot(item: Item) = findHotbarSlot { it?.item == item }

private fun findHotbarSlot(filter: (ItemStack?) -> Boolean) = findSlot(HOTBAR_SLOTS, filter)

/**
 * Checks if the inventory is full
 */
fun isInventoryFull() = mc.thePlayer.inventory.mainInventory
    .none { it == null }

/**
 * Checks if the hotbar is full
 */
fun isHotbarFull() = mc.thePlayer.inventory.mainInventory
    .filterIndexed { i, _ -> i < 9 }
    .none { it == null }

val BLOCK_BLACKLIST = listOf(
    Blocks.enchanting_table,
    Blocks.chest,
    Blocks.ender_chest,
    Blocks.trapped_chest,
    Blocks.anvil,
    Blocks.sand,
    Blocks.web,
    Blocks.torch,
    Blocks.crafting_table,
    Blocks.furnace,
    Blocks.waterlily,
    Blocks.dispenser,
    Blocks.stone_pressure_plate,
    Blocks.wooden_pressure_plate,
    Blocks.noteblock,
    Blocks.dropper,
    Blocks.tnt,
    Blocks.standing_banner,
    Blocks.wall_banner
)

fun findAutoBlockBlock(): Int {
    val blockSlot = findHotbarSlot { it?.item is ItemBlock }

    val block = (mc.thePlayer.inventoryContainer.getSlot(blockSlot).stack?.item as ItemBlock).block

    return if (block.isFullBlock && !BLOCK_BLACKLIST.contains(block))
        blockSlot
    else
        -1
}