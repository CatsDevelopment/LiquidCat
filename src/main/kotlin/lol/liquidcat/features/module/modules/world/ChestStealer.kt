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
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.utils.item.InventoryUtils
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntegerValue
import net.ccbluex.liquidbounce.features.module.modules.player.InventoryCleaner
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.Slot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import kotlin.random.Random

//TODO Add close delay option

@ModuleInfo("ChestStealer", "Automatically steals all items from a chest.", ModuleCategory.WORLD)
class ChestStealer : Module() {

    private val takeRandomizedValue = BoolValue("TakeRandomized", false)

    private val maxDelayValue: IntegerValue = object : IntegerValue("MaximumDelay", 250, 0, 500) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minDelayValue.get()

            if (i > newValue) set(i)

            nextDelay = TimeUtils.randomDelay(minDelayValue.get(), get())
        }
    }

    private val minDelayValue: IntegerValue = object : IntegerValue("MinimumDelay", 100, 0, 500) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxDelayValue.get()

            if (i < newValue) set(i)

            nextDelay = TimeUtils.randomDelay(get(), maxDelayValue.get())
        }
    }

    private val delayTimer = MSTimer()
    private var nextDelay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())

    private val closeValue = BoolValue("Close", true)

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val screen = mc.currentScreen

        if (screen is GuiChest)
            if (screen.lowerChestInventory.name.contains(ItemStack(Item.itemRegistry.getObject(ResourceLocation("minecraft:chest"))).displayName))
                if (!isEmpty(screen) && !InventoryUtils.isInventoryFull()) {
                    val slots = mutableListOf<Slot>()

                    for (i in 0 until screen.inventoryRows * 9) {
                        val slot = screen.inventorySlots.getSlot(i)

                        if (slot.hasStack) slots.add(slot)
                    }

                    if (delayTimer.hasTimePassed(nextDelay))
                        move(screen, if (takeRandomizedValue.get()) slots[Random.nextInt(slots.size)] else slots[0])
                } else if (closeValue.get()) mc.thePlayer.closeScreen()
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