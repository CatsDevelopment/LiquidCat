/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.combat

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.utils.item.InventoryUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntegerValue
import lol.liquidcat.value.ListValue
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.init.Items
import net.minecraft.network.play.client.*
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

//TODO Rewrite

@ModuleInfo(name = "AutoSoup", description = "Makes you automatically eat soup whenever your health is low.", category = ModuleCategory.COMBAT)
class AutoSoup : Module() {

    private val healthValue = FloatValue("Health", 15f, 0f, 20f)
    private val delayValue = IntegerValue("Delay", 150, 0, 500)
    private val openInventoryValue = BoolValue("OpenInv", false)
    private val simulateInventoryValue = BoolValue("SimulateInventory", true)
    private val bowlValue = ListValue("Bowl", arrayOf("Drop", "Move", "Stay"), "Drop")

    private val timer = MSTimer()

    override val tag: String
        get() = healthValue.get().toString()

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (!timer.hasTimePassed(delayValue.get().toLong()))
            return

        val soupInHotbar = InventoryUtils.findHotbarSlot(Items.mushroom_stew)
        if (mc.thePlayer.health <= healthValue.get() && soupInHotbar != -1) {
            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(soupInHotbar - 36))
            mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer
                    .getSlot(soupInHotbar).stack))
            if (bowlValue.get().equals("Drop", true))
                mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM,
                        BlockPos.ORIGIN, EnumFacing.DOWN))
            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
            timer.reset()
            return
        }

        val bowlInHotbar = InventoryUtils.findHotbarSlot(Items.bowl)
        if (bowlValue.get().equals("Move", true) && bowlInHotbar != -1) {
            if (openInventoryValue.get() && mc.currentScreen !is GuiInventory)
                return

            var bowlMovable = false

            for (i in 9..36) {
                val itemStack = mc.thePlayer.inventoryContainer.getSlot(i).stack

                if (itemStack == null) {
                    bowlMovable = true
                    break
                } else if (itemStack.item == Items.bowl && itemStack.stackSize < 64) {
                    bowlMovable = true
                    break
                }
            }

            if (bowlMovable) {
                val openInventory = mc.currentScreen !is GuiInventory && simulateInventoryValue.get()

                if (openInventory)
                    mc.netHandler.addToSendQueue(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))
                mc.playerController.windowClick(0, bowlInHotbar, 0, 1, mc.thePlayer)
            }
        }

        val soupInInventory = InventoryUtils.findInventorySlot(Items.mushroom_stew)
        if (soupInInventory != -1 && !InventoryUtils.isHotbarFull()) {
            if (openInventoryValue.get() && mc.currentScreen !is GuiInventory)
                return

            val openInventory = mc.currentScreen !is GuiInventory && simulateInventoryValue.get()
            if (openInventory)
                mc.netHandler.addToSendQueue(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))

            mc.playerController.windowClick(0, soupInInventory, 0, 1, mc.thePlayer)

            if (openInventory)
                mc.netHandler.addToSendQueue(C0DPacketCloseWindow())

            timer.reset()
        }
    }

}