/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.value.BoolValue
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.potion.Potion

@ModuleInfo(name = "Zoot", description = "Removes all bad potion effects/fire.", category = ModuleCategory.PLAYER)
class Zoot : Module() {

    private val badEffectsValue = BoolValue("BadEffects", true)
    private val fireValue = BoolValue("Fire", true)
    private val noAirValue = BoolValue("NoAir", false)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (noAirValue.get() && !mc.thePlayer.onGround)
            return

        if (badEffectsValue.get()) {
            val effect = mc.thePlayer.activePotionEffects.maxBy { it.duration }

            if (effect != null) {
                repeat(effect.duration / 20) {
                    mc.netHandler.addToSendQueue(C03PacketPlayer(mc.thePlayer.onGround))
                }
            }
        }


        if (fireValue.get() && !mc.thePlayer.capabilities.isCreativeMode && mc.thePlayer.isBurning) {
            repeat(9) {
                mc.netHandler.addToSendQueue(C03PacketPlayer(mc.thePlayer.onGround))
            }
        }
    }

    // TODO: Check current potion
    private fun hasBadEffect() = mc.thePlayer.isPotionActive(Potion.hunger) || mc.thePlayer.isPotionActive(Potion.moveSlowdown) ||
            mc.thePlayer.isPotionActive(Potion.digSlowdown) || mc.thePlayer.isPotionActive(Potion.harm) ||
            mc.thePlayer.isPotionActive(Potion.confusion) || mc.thePlayer.isPotionActive(Potion.blindness) ||
            mc.thePlayer.isPotionActive(Potion.weakness) || mc.thePlayer.isPotionActive(Potion.wither) || mc.thePlayer.isPotionActive(Potion.poison)

}