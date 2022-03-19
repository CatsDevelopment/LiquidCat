/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.world

import lol.liquidcat.LiquidCat
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Render3DEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.modules.player.InventoryCleaner
import lol.liquidcat.utils.item.isInventoryFull
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntValue
import lol.liquidcat.utils.timer.MSTimer
import lol.liquidcat.utils.timer.TimeUtils
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.Slot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

class ChestStealer : Module("ChestStealer", "Automatically steals all items from a chest.", ModuleCategory.WORLD) {

    private val random = BoolValue("TakeRandomized", false)
    private val title = BoolValue("CheckTitle", true)
    private val close = BoolValue("AutoClose", true)

    private val maxDelayValue: IntValue = object : IntValue("MaximumDelay", 250, 0..500) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minDelayValue.get()

            if (i > newValue) set(i)

            nextDelay = TimeUtils.randomDelay(minDelayValue.get(), get())
        }
    }

    private val minDelayValue: IntValue = object : IntValue("MinimumDelay", 100, 0..500) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxDelayValue.get()

            if (i < newValue) set(i)

            nextDelay = TimeUtils.randomDelay(get(), maxDelayValue.get())
        }
    }

    private val delayTimer = MSTimer()
    private var nextDelay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val screen = mc.currentScreen

        if (screen is GuiChest) {
            if (title.get() && !screen.lowerChestInventory.name.contains(ItemStack(Item.itemRegistry.getObject(ResourceLocation("minecraft:chest"))).displayName))
                return

            if (!delayTimer.hasTimePassed(nextDelay))
                return

            if (isEmpty(screen) && isInventoryFull()) {
                val slots = mutableListOf<Slot>()

                for (i in 0 until screen.inventoryRows * 9) {
                    val slot = screen.inventorySlots.getSlot(i)

                    if (slot.hasStack) slots.add(slot)
                }

                move(screen, if (random.get()) slots.random() else slots.first())
            } else if (close.get())
                mc.thePlayer.closeScreen()
        }
    }

    private fun move(screen: GuiChest, slot: Slot) {
        screen.handleMouseClick(slot, slot.slotNumber, 0, 1)
        delayTimer.reset()
        nextDelay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
    }

    private fun isEmpty(chest: GuiChest): Boolean {
        val inventoryCleaner = LiquidCat.moduleManager[InventoryCleaner::class.java] as InventoryCleaner

        for (i in 0 until chest.inventoryRows * 9) {
            val slot = chest.inventorySlots.getSlot(i)

            if (slot.hasStack || (inventoryCleaner.state && inventoryCleaner.isUseful(slot.stack, -1)))
                return false
        }

        return true
    }
}