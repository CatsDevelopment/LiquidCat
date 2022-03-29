/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.world

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Render3DEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.modules.player.InventoryCleaner
import lol.liquidcat.utils.item.isInventoryFull
import lol.liquidcat.utils.timer.MSTimer
import lol.liquidcat.utils.timer.TimeUtils
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntValue
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.Slot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

object ChestStealer : Module("ChestStealer", "Automatically steals all items from a chest.", ModuleCategory.WORLD) {

    private val random by BoolValue("TakeRandomized", false)
    private val title by BoolValue("CheckTitle", true)
    private val close by BoolValue("AutoClose", true)

    private val maxDelay: Int by object : IntValue("MaximumDelay", 250, 0..500) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minDelay

            if (i > newValue) set(i)

            nextDelay = TimeUtils.randomDelay(minDelay, get())
        }
    }

    private val minDelay: Int by object : IntValue("MinimumDelay", 100, 0..500) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxDelay

            if (i < newValue) set(i)

            nextDelay = TimeUtils.randomDelay(get(), maxDelay)
        }
    }

    private val delayTimer = MSTimer()
    private var nextDelay = TimeUtils.randomDelay(minDelay, maxDelay)

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val screen = mc.currentScreen

        if (screen is GuiChest) {
            if (title && !screen.lowerChestInventory.name.contains(ItemStack(Item.itemRegistry.getObject(ResourceLocation("minecraft:chest"))).displayName))
                return

            if (!delayTimer.hasTimePassed(nextDelay))
                return

            if (isEmpty(screen) && isInventoryFull()) {
                val slots = mutableListOf<Slot>()

                for (i in 0 until screen.inventoryRows * 9) {
                    val slot = screen.inventorySlots.getSlot(i)

                    if (slot.hasStack) slots.add(slot)
                }

                move(screen, if (random) slots.random() else slots.first())
            } else if (close)
                mc.thePlayer.closeScreen()
        }
    }

    private fun move(screen: GuiChest, slot: Slot) {
        screen.handleMouseClick(slot, slot.slotNumber, 0, 1)
        delayTimer.reset()
        nextDelay = TimeUtils.randomDelay(minDelay, maxDelay)
    }

    private fun isEmpty(chest: GuiChest): Boolean {
        for (i in 0 until chest.inventoryRows * 9) {
            val slot = chest.inventorySlots.getSlot(i)

            if (slot.hasStack || (InventoryCleaner.state && InventoryCleaner.isUseful(slot.stack, -1)))
                return false
        }

        return true
    }
}