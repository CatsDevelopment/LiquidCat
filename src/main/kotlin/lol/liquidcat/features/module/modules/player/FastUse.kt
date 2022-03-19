/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.player

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.entity.drinking
import lol.liquidcat.utils.entity.eating
import lol.liquidcat.utils.sendPacket
import lol.liquidcat.value.IntValue
import net.minecraft.network.play.client.C03PacketPlayer

class FastUse : Module("FastUse", "Allows you to use items faster.", ModuleCategory.PLAYER) {

    private val delayValue = IntValue("Delay", 16, 1..32)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if ((mc.thePlayer.eating || mc.thePlayer.drinking) && mc.thePlayer.itemInUseDuration >= delayValue.get()) {
            repeat(33 - mc.thePlayer.itemInUseDuration) {
                sendPacket(C03PacketPlayer(mc.thePlayer.onGround))
            }

            mc.playerController.onStoppedUsingItem(mc.thePlayer)
        }
    }
}