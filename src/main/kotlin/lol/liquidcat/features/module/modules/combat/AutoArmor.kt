/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.combat

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Render3DEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.utils.entity.moving
import lol.liquidcat.utils.item.ArmorComparator
import lol.liquidcat.utils.item.ArmorPiece
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntegerValue
import net.ccbluex.liquidbounce.injection.implementations.IItemStack
import lol.liquidcat.utils.item.InventoryUtils
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.item.ItemArmor
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.client.C0DPacketCloseWindow
import net.minecraft.network.play.client.C16PacketClientStatus
import java.util.stream.Collectors
import java.util.stream.IntStream

//TODO Rewrite

@ModuleInfo(
    name = "AutoArmor",
    description = "Automatically equips the best armor in your inventory.",
    category = ModuleCategory.COMBAT
)
class AutoArmor : Module() {
    private val maxDelayValue: IntegerValue = object : IntegerValue("MaxDelay", 200, 0, 400) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val minDelay = minDelayValue.get()
            if (minDelay > newValue) set(minDelay)
        }
    }
    private val minDelayValue: IntegerValue = object : IntegerValue("MinDelay", 100, 0, 400) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val maxDelay = maxDelayValue.get()
            if (maxDelay < newValue) set(maxDelay)
        }
    }
    private val invOpenValue = BoolValue("InvOpen", false)
    private val simulateInventory = BoolValue("SimulateInventory", true)
    private val noMoveValue = BoolValue("NoMove", false)
    private val itemDelayValue = IntegerValue("ItemDelay", 0, 0, 5000)
    private val hotbarValue = BoolValue("Hotbar", true)

    private var delay: Long = 0

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (!InventoryUtils.CLICK_TIMER.hasTimePassed(delay) || mc.thePlayer.openContainer != null && mc.thePlayer.openContainer.windowId != 0) return

        // Find best armor
        val armorPieces = IntStream.range(0, 36)
            .filter { i: Int -> val itemStack = mc.thePlayer.inventory.getStackInSlot(i)
                (itemStack != null && itemStack.item is ItemArmor && (i < 9 || System.currentTimeMillis() - (itemStack as Any as IItemStack).itemDelay >= itemDelayValue.get()))
            }
            .mapToObj { i: Int -> ArmorPiece(mc.thePlayer.inventory.getStackInSlot(i), i) }
            .collect(Collectors.groupingBy(ArmorPiece::armorType))

        val bestArmor = arrayOfNulls<ArmorPiece>(4)

        for ((key, value) in armorPieces) {
            bestArmor[key] = value.stream().max(ARMOR_COMPARATOR).orElse(null)
        }

        // Swap armor
        for (i in 0..3) {
            val armorPiece = bestArmor[i] ?: continue
            val armorSlot = 3 - i
            val oldArmor = ArmorPiece(mc.thePlayer.inventory.armorItemInSlot(armorSlot), -1)

            if (oldArmor.itemStack == null || oldArmor.itemStack.item !is ItemArmor || ARMOR_COMPARATOR.compare(oldArmor, armorPiece) < 0) {
                if (oldArmor.itemStack != null && move(8 - armorSlot, true)) return

                if (mc.thePlayer.inventory.armorItemInSlot(armorSlot) == null && move(armorPiece.slot, false)) return
            }
        }
    }

    /**
     * Shift+Left clicks the specified item
     *
     * @param item        Slot of the item to click
     * @param isArmorSlot
     * @return True if it is unable to move the item
     */
    private fun move(item: Int, isArmorSlot: Boolean): Boolean {
        if (!isArmorSlot && item < 9 && hotbarValue.get() && mc.currentScreen !is GuiInventory) {
            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(item))
            mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer.getSlot(item).stack))
            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
            return true
        } else if (!(noMoveValue.get() && mc.thePlayer.moving) && (!invOpenValue.get() || mc.currentScreen is GuiInventory) && item != -1) {
            val openInventory = simulateInventory.get() && mc.currentScreen !is GuiInventory

            if (openInventory) mc.netHandler.addToSendQueue(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))

            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, if (isArmorSlot) item else if (item < 9) item + 36 else item, 0, 1, mc.thePlayer)
            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
            if (openInventory) mc.netHandler.addToSendQueue(C0DPacketCloseWindow())
            return true
        }
        return false
    }

    companion object {
        val ARMOR_COMPARATOR = ArmorComparator()
    }
}