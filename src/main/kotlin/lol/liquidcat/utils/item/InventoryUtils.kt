/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.item

import lol.liquidcat.event.ClickWindowEvent
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Listenable
import lol.liquidcat.event.PacketEvent
import lol.liquidcat.utils.mc
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement

object InventoryUtils : Listenable {

    @EventTarget
    fun onClick(event: ClickWindowEvent) {
        CLICK_TIMER.reset()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is C08PacketPlayerBlockPlacement)
            CLICK_TIMER.reset()
    }

    override fun handleEvents(): Boolean {
        return true
    }

    val CLICK_TIMER = MSTimer()

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

    fun isInventoryFull() = mc.thePlayer.inventory.mainInventory.none { it == null }

    fun isHotbarFull() = mc.thePlayer.inventory.mainInventory.filterIndexed { i, _ -> i < 9 }.none { it == null }

    /**
     * Returns the slot index of the searched [item] in the inventory
     */
    fun findInventorySlot(item: Item) = findInventorySlot { it?.item == item }

    private fun findInventorySlot(predicate: (ItemStack?) -> Boolean): Int {
        return (9..35).firstOrNull { predicate(mc.thePlayer.inventoryContainer.getSlot(it).stack) } ?: -1
    }

    /**
     * Returns the slot index of the searched [item] in the hotbar
     */
    fun findHotbarSlot(item: Item) = findHotbarSlot { it?.item == item }

    private fun findHotbarSlot(predicate: (ItemStack?) -> Boolean): Int {
        return (36..44).firstOrNull { predicate(mc.thePlayer.inventoryContainer.getSlot(it).stack) } ?: -1
    }

    fun findAutoBlockBlock(): Int {
        val blockSlot = findHotbarSlot { it?.item is ItemBlock }

        val block = (mc.thePlayer.inventoryContainer.getSlot(blockSlot) as ItemBlock).block

        return if (block.isFullBlock && !BLOCK_BLACKLIST.contains(block))
            blockSlot
        else
            -1
    }
}