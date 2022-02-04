/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.MotionEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo

import net.ccbluex.liquidbounce.utils.InventoryUtils
import lol.liquidcat.value.ListValue
import net.minecraft.init.Items
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange

@ModuleInfo(name = "KeepAlive", description = "Tries to prevent you from dying.", category = ModuleCategory.PLAYER)
class KeepAlive : Module() {

    val modeValue = ListValue("Mode", arrayOf("/heal", "Soup"), "/heal")

    private var runOnce = false

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (mc.thePlayer.isDead || mc.thePlayer.health <= 0) {
            if (runOnce) return

            when (modeValue.get().toLowerCase()) {
                "/heal" -> mc.thePlayer.sendChatMessage("/heal")
                "soup" -> {
                    val soupInHotbar = InventoryUtils.findItem(36, 45, Items.mushroom_stew)

                    if (soupInHotbar != -1) {
                        mc.netHandler.addToSendQueue(C09PacketHeldItemChange(soupInHotbar - 36))
                        mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer.getSlot(soupInHotbar).stack))
                        mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                    }
                }
            }

            runOnce = true
        } else
            runOnce = false
    }
}