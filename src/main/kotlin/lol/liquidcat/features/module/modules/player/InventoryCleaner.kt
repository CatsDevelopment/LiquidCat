/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.player

import lol.liquidcat.LiquidCat
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.modules.combat.AutoArmor
import lol.liquidcat.utils.click.ClickHandler
import lol.liquidcat.utils.entity.moving
import lol.liquidcat.utils.item.ArmorPiece
import lol.liquidcat.utils.item.BLOCK_BLACKLIST
import lol.liquidcat.utils.item.getEnchantment
import lol.liquidcat.utils.sendPacket
import lol.liquidcat.utils.timer.TimeUtils
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntValue
import lol.liquidcat.value.ListValue
import net.ccbluex.liquidbounce.injection.implementations.IItemStack
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.enchantment.Enchantment
import net.minecraft.init.Blocks
import net.minecraft.item.*
import net.minecraft.network.play.client.C0DPacketCloseWindow
import net.minecraft.network.play.client.C16PacketClientStatus

object InventoryCleaner : Module("InventoryCleaner", "Automatically throws away useless items.", ModuleCategory.PLAYER) {

    /**
     * OPTIONS
     */

    private val maxDelay: Int by object : IntValue("MaxDelay", 600, 0..1000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val minCPS = minDelay
            if (minCPS > newValue) set(minCPS)
        }
    }

    private val minDelay: Int by object : IntValue("MinDelay", 400, 0..1000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val maxDelay = maxDelay
            if (maxDelay < newValue) set(maxDelay)
        }
    }

    private val invOpen by BoolValue("InvOpen", false)
    private val simulateInv by BoolValue("SimulateInventory", true)
    private val noMove by BoolValue("NoMove", false)
    private val ignoreVehicles by BoolValue("IgnoreVehicles", false)
    private val hotbar by BoolValue("Hotbar", true)
    private val randomSlot by BoolValue("RandomSlot", false)
    private val sort by BoolValue("Sort", true)
    private val itemDelay by IntValue("ItemDelay", 0, 0..5000)

    private val items = arrayOf("None", "Ignore", "Sword", "Bow", "Pickaxe", "Axe", "Food", "Block", "Water", "Gapple", "Pearl")
    private val sortSlot1 by ListValue("SortSlot-1", items, "Sword")
    private val sortSlot2 by ListValue("SortSlot-2", items, "Bow")
    private val sortSlot3 by ListValue("SortSlot-3", items, "Pickaxe")
    private val sortSlot4 by ListValue("SortSlot-4", items, "Axe")
    private val sortSlot5 by ListValue("SortSlot-5", items, "None")
    private val sortSlot6 by ListValue("SortSlot-6", items, "None")
    private val sortSlot7 by ListValue("SortSlot-7", items, "Food")
    private val sortSlot8 by ListValue("SortSlot-8", items, "Block")
    private val sortSlot9 by ListValue("SortSlot-9", items, "Block")

    /**
     * VALUES
     */

    private var delay = 0L

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!ClickHandler.CLICK_TIMER.hasTimePassed(delay) ||
                mc.currentScreen !is GuiInventory && invOpen ||
                noMove && mc.thePlayer.moving ||
                mc.thePlayer.openContainer != null && mc.thePlayer.openContainer.windowId != 0)
            return

        if (sort)
            sortHotbar()

        while (ClickHandler.CLICK_TIMER.hasTimePassed(delay)) {
            val garbageItems = items(9, if (hotbar) 45 else 36)
                    .filter { !isUseful(it.value, it.key) }
                    .keys
                    .toMutableList()

            // Shuffle items
            if (randomSlot)
                garbageItems.shuffle()

            val garbageItem = garbageItems.firstOrNull() ?: break

            // Drop all useless items
            val openInventory = mc.currentScreen !is GuiInventory && simulateInv

            if (openInventory)
                sendPacket(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))

            mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, garbageItem, 4, 4, mc.thePlayer)

            if (openInventory)
                sendPacket(C0DPacketCloseWindow())

            delay = TimeUtils.randomDelay(minDelay, maxDelay)
        }
    }

    /**
     * Checks if the item is useful
     *
     * @param slot Slot id of the item. If the item isn't in the inventory -1
     * @return Returns true when the item is useful
     */
    fun isUseful(itemStack: ItemStack, slot: Int): Boolean {
        return try {
            val item = itemStack.item

            if (item is ItemSword || item is ItemTool) {
                if (slot >= 36 && findBetterItem(slot - 36, mc.thePlayer.inventory.getStackInSlot(slot - 36)) == slot - 36)
                    return true

                for (i in 0..8) {
                    if (type(i).equals("sword", true) && item is ItemSword
                            || type(i).equals("pickaxe", true) && item is ItemPickaxe
                            || type(i).equals("axe", true) && item is ItemAxe) {
                        if (findBetterItem(i, mc.thePlayer.inventory.getStackInSlot(i)) == null) {
                            return true
                        }
                    }
                }

                val damage = (itemStack.attributeModifiers["generic.attackDamage"].firstOrNull()?.amount
                        ?: 0.0) + 1.25 * itemStack.getEnchantment(Enchantment.sharpness)

                items(0, 45).none { (_, stack) ->
                    stack != itemStack && stack.javaClass == itemStack.javaClass
                            && damage <= (stack.attributeModifiers["generic.attackDamage"].firstOrNull()?.amount
                            ?: 0.0) + 1.25 * stack.getEnchantment(Enchantment.sharpness)
                }
            } else if (item is ItemBow) {
                val currPower = itemStack.getEnchantment(Enchantment.power)

                items().none { (_, stack) ->
                    itemStack != stack && stack.item is ItemBow &&
                            currPower <= stack.getEnchantment(Enchantment.power)
                }
            } else if (item is ItemArmor) {
                val currArmor = ArmorPiece(itemStack, slot)

                items().none { (slot, stack) ->
                    if (stack != itemStack && stack.item is ItemArmor) {
                        val armor = ArmorPiece(stack, slot)

                        if (armor.armorType != currArmor.armorType)
                            false
                        else
                            AutoArmor.ARMOR_COMPARATOR.compare(currArmor, armor) <= 0
                    } else
                        false
                }
            } else if (itemStack.unlocalizedName == "item.compass") {
                items(0, 45).none { (_, stack) -> itemStack != stack && stack.unlocalizedName == "item.compass" }
            } else item is ItemFood || itemStack.unlocalizedName == "item.arrow" ||
                    item is ItemBlock && !itemStack.unlocalizedName.contains("flower") ||
                    item is ItemBed || itemStack.unlocalizedName == "item.diamond" || itemStack.unlocalizedName == "item.ingotIron" ||
                    item is ItemPotion || item is ItemEnderPearl || item is ItemEnchantedBook || item is ItemBucket || itemStack.unlocalizedName == "item.stick" || 
                    ignoreVehicles && (item is ItemBoat || item is ItemMinecart)
        } catch (ex: Exception) {
            LiquidCat.logger.error("(InventoryCleaner) Failed to check item: ${itemStack.unlocalizedName}.", ex)

            true
        }
    }

    /**
     * INVENTORY SORTER
     */

    /**
     * Sort hotbar
     */
    private fun sortHotbar() {
        for (index in 0..8) {
            val bestItem = findBetterItem(index, mc.thePlayer.inventory.getStackInSlot(index)) ?: continue

            if (bestItem != index) {
                val openInventory = mc.currentScreen !is GuiInventory && simulateInv

                if (openInventory)
                    sendPacket(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))

                mc.playerController.windowClick(0, if (bestItem < 9) bestItem + 36 else bestItem, index,
                        2, mc.thePlayer)

                if (openInventory)
                    sendPacket(C0DPacketCloseWindow())

                delay = TimeUtils.randomDelay(minDelay, maxDelay)
                break
            }
        }
    }

    private fun findBetterItem(targetSlot: Int, slotStack: ItemStack?): Int? {
        val type = type(targetSlot)

        when (type.lowercase()) {
            "sword", "pickaxe", "axe" -> {
                val currentType: Class<out Item> = when {
                    type.equals("Sword", ignoreCase = true) -> ItemSword::class.java
                    type.equals("Pickaxe", ignoreCase = true) -> ItemPickaxe::class.java
                    type.equals("Axe", ignoreCase = true) -> ItemAxe::class.java
                    else -> return null
                }

                var bestWeapon = if (slotStack?.item?.javaClass == currentType)
                    targetSlot
                else -1

                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, itemStack ->
                    if (itemStack?.item?.javaClass == currentType && !type(index).equals(type, ignoreCase = true)) {
                        if (bestWeapon == -1) {
                            bestWeapon = index
                        } else {
                            val currDamage = (itemStack.attributeModifiers["generic.attackDamage"].firstOrNull()?.amount
                                    ?: 0.0) + 1.25 * itemStack.getEnchantment(Enchantment.sharpness)

                            val bestStack = mc.thePlayer.inventory.getStackInSlot(bestWeapon)
                                    ?: return@forEachIndexed
                            val bestDamage = (bestStack.attributeModifiers["generic.attackDamage"].firstOrNull()?.amount
                                    ?: 0.0) + 1.25 * bestStack.getEnchantment(Enchantment.sharpness)

                            if (bestDamage < currDamage)
                                bestWeapon = index
                        }
                    }
                }

                return if (bestWeapon != -1 || bestWeapon == targetSlot) bestWeapon else null
            }

            "bow" -> {
                var bestBow = if (slotStack?.item is ItemBow) targetSlot else -1
                var bestPower = if (bestBow != -1)
                    slotStack.getEnchantment(Enchantment.power)
                else
                    0

                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, itemStack ->
                    if (itemStack?.item is ItemBow && !type(index).equals(type, ignoreCase = true)) {
                        if (bestBow == -1) {
                            bestBow = index
                        } else {
                            val power = itemStack.getEnchantment(Enchantment.power)

                            if (itemStack.getEnchantment(Enchantment.power) > bestPower) {
                                bestBow = index
                                bestPower = power
                            }
                        }
                    }
                }

                return if (bestBow != -1) bestBow else null
            }

            "food" -> {
                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, stack ->
                    val item = stack?.item

                    if (item is ItemFood && item !is ItemAppleGold && !type(index).equals("Food", ignoreCase = true)) {
                        val replaceCurr = slotStack == null || slotStack.item !is ItemFood

                        return if (replaceCurr) index else null
                    }
                }
            }

            "block" -> {
                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, stack ->
                    val item = stack?.item

                    if (item is ItemBlock && !BLOCK_BLACKLIST.contains(item.block) &&
                            !type(index).equals("Block", ignoreCase = true)) {
                        val replaceCurr = slotStack == null || slotStack.item !is ItemBlock

                        return if (replaceCurr) index else null
                    }
                }
            }

            "water" -> {
                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, stack ->
                    val item = stack?.item

                    if (item is ItemBucket && item.isFull == Blocks.flowing_water && !type(index).equals("Water", ignoreCase = true)) {
                        val replaceCurr = slotStack == null || slotStack.item !is ItemBucket || (slotStack.item as ItemBucket).isFull != Blocks.flowing_water

                        return if (replaceCurr) index else null
                    }
                }
            }

            "gapple" -> {
                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, stack ->
                    val item = stack?.item

                    if (item is ItemAppleGold && !type(index).equals("Gapple", ignoreCase = true)) {
                        val replaceCurr = slotStack == null || slotStack.item !is ItemAppleGold

                        return if (replaceCurr) index else null
                    }
                }
            }

            "pearl" -> {
                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, stack ->
                    val item = stack?.item

                    if (item is ItemEnderPearl && !type(index).equals("Pearl", ignoreCase = true)) {
                        val replaceCurr = slotStack == null || slotStack.item !is ItemEnderPearl

                        return if (replaceCurr) index else null
                    }
                }
            }
        }

        return null
    }

    /**
     * Get items in inventory
     */
    private fun items(start: Int = 0, end: Int = 45): Map<Int, ItemStack> {
        val items = mutableMapOf<Int, ItemStack>()

        for (i in end - 1 downTo start) {
            val itemStack = mc.thePlayer.inventoryContainer.getSlot(i).stack ?: continue
            itemStack.item ?: continue

            if (i in 36..44 && type(i).equals("Ignore", ignoreCase = true))
                continue

            if (System.currentTimeMillis() - (itemStack as IItemStack).itemDelay >= itemDelay)
                items[i] = itemStack
        }

        return items
    }

    /**
     * Get type of [targetSlot]
     */
    private fun type(targetSlot: Int) = when (targetSlot) {
        0 -> sortSlot1
        1 -> sortSlot2
        2 -> sortSlot3
        3 -> sortSlot4
        4 -> sortSlot5
        5 -> sortSlot6
        6 -> sortSlot7
        7 -> sortSlot8
        8 -> sortSlot9
        else -> ""
    }
}
