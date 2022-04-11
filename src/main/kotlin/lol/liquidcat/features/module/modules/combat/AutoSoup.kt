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
import lol.liquidcat.utils.item.findHotbarSlot
import lol.liquidcat.utils.item.findInventorySlot
import lol.liquidcat.utils.item.isHotbarFull
import lol.liquidcat.utils.sendPacket
import lol.liquidcat.utils.timer.MSTimer
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntValue
import lol.liquidcat.value.ListValue
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.init.Items
import net.minecraft.network.play.client.*
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

//TODO Rewrite

object AutoSoup : Module("AutoSoup", "Makes you automatically eat soup whenever your health is low.", ModuleCategory.COMBAT) {

    private val health by FloatValue("Health", 15f, 0f..20f)
    private val delay by IntValue("Delay", 150, 0..500)
    private val openInv by BoolValue("OpenInv", false)
    private val simulateInv by BoolValue("SimulateInventory", true)
    private val bowl by ListValue("Bowl", arrayOf("Drop", "Move", "Stay"), "Drop")

    private val timer = MSTimer()

    override val tag: String
        get() = health.toString()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!timer.hasTimePassed(delay.toLong()))
            return

        val soupInHotbar = findHotbarSlot(Items.mushroom_stew)
        if (mc.thePlayer.health <= health && soupInHotbar != -1) {
            sendPacket(C09PacketHeldItemChange(soupInHotbar - 36))
            sendPacket(C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer.getSlot(soupInHotbar).stack))
            if (bowl.equals("Drop", true))
                sendPacket(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
            sendPacket(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
            timer.reset()
            return
        }

        val bowlInHotbar = findHotbarSlot(Items.bowl)
        if (bowl.equals("Move", true) && bowlInHotbar != -1) {
            if (openInv && mc.currentScreen !is GuiInventory)
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
                val openInventory = mc.currentScreen !is GuiInventory && simulateInv

                if (openInventory)
                    sendPacket(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))
                mc.playerController.windowClick(0, bowlInHotbar, 0, 1, mc.thePlayer)
            }
        }

        val soupInInventory = findInventorySlot(Items.mushroom_stew)
        if (soupInInventory != -1 && !isHotbarFull()) {
            if (openInv && mc.currentScreen !is GuiInventory)
                return

            val openInventory = mc.currentScreen !is GuiInventory && simulateInv
            if (openInventory)
                sendPacket(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))

            mc.playerController.windowClick(0, soupInInventory, 0, 1, mc.thePlayer)

            if (openInventory)
                sendPacket(C0DPacketCloseWindow())

            timer.reset()
        }
    }
}