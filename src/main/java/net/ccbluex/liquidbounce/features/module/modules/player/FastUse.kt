/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.value.IntegerValue
import net.minecraft.item.ItemBucketMilk
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemPotion
import net.minecraft.network.play.client.C03PacketPlayer

@ModuleInfo(name = "FastUse", description = "Allows you to use items faster.", category = ModuleCategory.PLAYER)
class FastUse : Module() {

    private val delayValue = IntegerValue("Delay", 16, 1, 32)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.isUsingItem) {
            val usingItem = mc.thePlayer.itemInUse.item

            if ((usingItem !is ItemFood && usingItem !is ItemBucketMilk && usingItem !is ItemPotion))
                return

            if (mc.thePlayer.itemInUseDuration >= delayValue.get()) {
                repeat(33 - mc.thePlayer.itemInUseDuration) {
                    mc.netHandler.addToSendQueue(C03PacketPlayer(mc.thePlayer.onGround))
                }
                mc.playerController.onStoppedUsingItem(mc.thePlayer)
            }
        }
    }
}