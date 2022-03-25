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
import lol.liquidcat.utils.entity.moving
import lol.liquidcat.utils.item.ArmorComparator
import lol.liquidcat.utils.item.ArmorPiece
import lol.liquidcat.utils.ClickHandler
import lol.liquidcat.utils.sendPacket
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntValue
import net.ccbluex.liquidbounce.injection.implementations.IItemStack
import lol.liquidcat.utils.timer.TimeUtils
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.item.ItemArmor
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.client.C0DPacketCloseWindow
import net.minecraft.network.play.client.C16PacketClientStatus
import java.util.stream.Collectors
import java.util.stream.IntStream

//TODO Rewrite

object AutoArmor : Module("AutoArmor", "Automatically equips the best armor in your inventory.", ModuleCategory.COMBAT) {

    private val maxDelay: Int by object : IntValue("MaxDelay", 200, 0..400) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val minDelay = minDelay
            if (minDelay > newValue) set(minDelay)
        }
    }
    private val minDelay: Int by object : IntValue("MinDelay", 100, 0..400) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val maxDelay = maxDelay
            if (maxDelay < newValue) set(maxDelay)
        }
    }
    private val invOpen by BoolValue("InvOpen", false)
    private val simulateInv by BoolValue("SimulateInventory", true)
    private val noMove by BoolValue("NoMove", false)
    private val itemDelay by IntValue("ItemDelay", 0, 0..5000)
    private val hotbar by BoolValue("Hotbar", true)

    private var delay: Long = 0
    val ARMOR_COMPARATOR = ArmorComparator()

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (!ClickHandler.CLICK_TIMER.hasTimePassed(delay) || mc.thePlayer.openContainer != null && mc.thePlayer.openContainer.windowId != 0) return

        // Find best armor
        val armorPieces = IntStream.range(0, 36)
            .filter { i: Int -> val itemStack = mc.thePlayer.inventory.getStackInSlot(i)
                (itemStack != null && itemStack.item is ItemArmor && (i < 9 || System.currentTimeMillis() - (itemStack as Any as IItemStack).itemDelay >= itemDelay))
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
        if (!isArmorSlot && item < 9 && hotbar && mc.currentScreen !is GuiInventory) {
            sendPacket(C09PacketHeldItemChange(item))
            sendPacket(C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer.getSlot(item).stack))
            sendPacket(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
            delay = TimeUtils.randomDelay(minDelay, maxDelay)
            return true
        } else if (!(noMove && mc.thePlayer.moving) && (!invOpen || mc.currentScreen is GuiInventory) && item != -1) {
            val openInventory = simulateInv && mc.currentScreen !is GuiInventory

            if (openInventory) sendPacket(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))

            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, if (isArmorSlot) item else if (item < 9) item + 36 else item, 0, 1, mc.thePlayer)
            delay = TimeUtils.randomDelay(minDelay, maxDelay)
            if (openInventory) sendPacket(C0DPacketCloseWindow())
            return true
        }
        return false
    }
}